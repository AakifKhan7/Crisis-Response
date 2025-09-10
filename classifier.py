import multiprocessing
import torch
from torch import nn
from torchvision import datasets, transforms, models
from torch.utils.data import DataLoader, random_split
from PIL import Image, UnidentifiedImageError, ImageFile
import os
import kagglehub

# Allow loading of truncated/corrupt images
ImageFile.LOAD_TRUNCATED_IMAGES = True


# =========================
# 1. Safe loader
# =========================
def safe_loader(path: str):
    try:
        with open(path, "rb") as f:
            img = Image.open(f)
            return img.convert("RGB")
    except (UnidentifiedImageError, OSError) as e:
        print(f"⚠️ Skipping corrupted image: {path} ({e})")
        return None


# Custom ImageFolder that skips None samples
class CleanImageFolder(datasets.ImageFolder):
    def __getitem__(self, index):
        while True:
            path, target = self.samples[index]
            sample = self.loader(path)
            if sample is None:  # bad image → skip forward
                index = (index + 1) % len(self.samples)
                continue
            if self.transform is not None:
                sample = self.transform(sample)
            return sample, target


def main():
    # =========================
    # 2. Dataset Path
    # =========================
    # dataset_path = r"C:\Users\Anzar\Desktop\Crisis Response\dataset"
    
    path = kagglehub.dataset_download("varpit94/disaster-images-dataset")

    dataset_path = os.path.join(path, "Comprehensive Disaster Dataset(CDD)")
    print("Path to dataset files:", dataset_path)


    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print("Using device:", device)

    # =========================
    # 3. Transforms
    # =========================
    train_transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.RandomHorizontalFlip(p=0.5),
        transforms.RandomRotation(25),
        transforms.RandomResizedCrop(224, scale=(0.7, 1.0)),
        transforms.ColorJitter(brightness=0.3, contrast=0.3, saturation=0.3, hue=0.1),
        transforms.RandomGrayscale(p=0.1),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]),
    ])

    val_transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]),
    ])

    # =========================
    # 4. Dataset & DataLoader
    # =========================
    full_dataset = CleanImageFolder(dataset_path, transform=train_transform, loader=safe_loader)
    dataset_size = len(full_dataset)
    train_size = int(0.8 * dataset_size)
    val_size = dataset_size - train_size

    train_dataset, val_dataset = random_split(full_dataset, [train_size, val_size])
    val_dataset.dataset.transform = val_transform  # force val set deterministic

    train_loader = DataLoader(train_dataset, batch_size=32, shuffle=True, num_workers=0)
    val_loader = DataLoader(val_dataset, batch_size=32, shuffle=False, num_workers=0)

    print("Classes:", full_dataset.classes)
    print("Train samples:", len(train_dataset), " Val samples:", len(val_dataset))

    # =========================
    # 5. Model
    # =========================
    model = models.resnet18(weights=models.ResNet18_Weights.DEFAULT)

    # Freeze most layers, train last layers
    for name, param in model.named_parameters():
        if "layer2" in name or "layer3" in name or "layer4" in name or "fc" in name:
            param.requires_grad = True
        else:
            param.requires_grad = False

    model.fc = nn.Sequential(
        nn.Dropout(0.5),
        nn.Linear(model.fc.in_features, len(full_dataset.classes))
    )
    model = model.to(device)

    # =========================
    # 6. Loss & Optimizer
    # =========================
    criterion = nn.CrossEntropyLoss(label_smoothing=0.1)
    optimizer = torch.optim.Adam(
        filter(lambda p: p.requires_grad, model.parameters()),
        lr=1e-4,
        weight_decay=1e-4,
    )
    scheduler = torch.optim.lr_scheduler.StepLR(optimizer, step_size=5, gamma=0.1)

    # =========================
    # 7. Training Loop
    # =========================
    best_acc = 0.0
    num_epochs = 20

    for epoch in range(num_epochs):
        model.train()
        running_loss = 0.0

        for imgs, labels in train_loader:
            imgs, labels = imgs.to(device), labels.to(device)

            optimizer.zero_grad()
            outputs = model(imgs)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()

            running_loss += loss.item()
            print(f"\rEpoch {epoch+1}/{num_epochs} - Loss: {loss.item():.4f}", end="")

        # Validation
        model.eval()
        correct, total = 0, 0
        with torch.no_grad():
            for imgs, labels in val_loader:
                imgs, labels = imgs.to(device), labels.to(device)
                outputs = model(imgs)
                _, preds = torch.max(outputs, 1)
                correct += (preds == labels).sum().item()
                total += labels.size(0)

        acc = correct / total
        avg_loss = running_loss / max(1, len(train_loader))
        print(f"Epoch {epoch+1}/{num_epochs}: Loss={avg_loss:.4f}, Val Acc={acc:.4f}")

        scheduler.step()

        # Save best model
        if acc > best_acc:
            torch.save(model.state_dict(), "best_disaster_model.pth")
            best_acc = acc
            print(f"✅ Saved new best model (Acc={best_acc:.4f})")

    # =========================
    # 8. Save final model
    # =========================
    torch.save(model.state_dict(), "disaster_model_final.pth")
    print("Training complete. Best Val Acc:", best_acc)


if __name__ == "__main__":
    multiprocessing.freeze_support()
    main()
