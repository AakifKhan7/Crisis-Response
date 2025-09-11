from datasets import Dataset
import pandas as pd
import torch
from torch import nn
from torch.utils.data import DataLoader
from transformers import AutoTokenizer, AutoModel, DataCollatorWithPadding
from torch.optim import AdamW
from sklearn.metrics import classification_report

# ==== Sample Data ====
data = {
    "text": [
        "My father is stuck and needs an ambulance near Andheri East.",
        "We need food and water urgently in Bandra.",
        "We are safe but need shelter after flood destroyed our home.",
        "Low battery, we are okay for now but stranded.",
        "There's a fire, people trapped in the building!"
    ],
    "labels": [
        "medical_critical",
        "food_critical",
        "shelter_moderate",
        "rescue_low",
        "rescue_critical"
    ]
}

# ==== Prepare Dataset ====
df = pd.DataFrame(data)
df[['type', 'severity']] = df['labels'].str.split('_', expand=True)

dataset = Dataset.from_pandas(df)

type_labels = df['type'].unique().tolist()         # ['medical', 'food', 'shelter', 'rescue']
severity_labels = df['severity'].unique().tolist() # ['critical', 'moderate', 'low']

print("Emergency Types:", type_labels)
print("Severities:", severity_labels)

# ==== Tokenizer & Preprocessing ====
tokenizer = AutoTokenizer.from_pretrained("bert-base-uncased")

def preprocess(example):
    enc = tokenizer(example["text"], truncation=True, padding="max_length", max_length=128)
    enc["label_type"] = int(type_labels.index(example["type"]))
    enc["label_severity"] = int(severity_labels.index(example["severity"]))
    return enc

tokenized_dataset = dataset.map(preprocess)

# Set format for PyTorch tensors
tokenized_dataset.set_format(
    type="torch",
    columns=["input_ids", "attention_mask", "label_type", "label_severity"]
)

# ==== Data Collator for Batching ====
data_collator = DataCollatorWithPadding(tokenizer=tokenizer, return_tensors="pt")
train_loader = DataLoader(tokenized_dataset, batch_size=2, collate_fn=data_collator)

# ==== Model Definition ====
class CrisisClassifier(nn.Module):
    def __init__(self, base_model="bert-base-uncased", num_types=4, num_severities=3):
        super().__init__()
        self.bert = AutoModel.from_pretrained(base_model)
        self.dropout = nn.Dropout(0.3)
        self.type_classifier = nn.Linear(768, num_types)
        self.severity_classifier = nn.Linear(768, num_severities)

    def forward(self, input_ids, attention_mask):
        output = self.bert(input_ids=input_ids, attention_mask=attention_mask)
        pooled = self.dropout(output.pooler_output)
        type_logits = self.type_classifier(pooled)
        severity_logits = self.severity_classifier(pooled)
        return type_logits, severity_logits

# ==== Training Loop ====
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = CrisisClassifier(num_types=len(type_labels), num_severities=len(severity_labels)).to(device)

optimizer = AdamW(model.parameters(), lr=5e-5)
loss_fn = nn.CrossEntropyLoss()

model.train()
for epoch in range(3):
    print(f"\nEpoch {epoch + 1}")
    for batch in train_loader:
        input_ids = batch["input_ids"].to(device)
        attention_mask = batch["attention_mask"].to(device)
        labels_type = batch["label_type"].to(device)
        labels_severity = batch["label_severity"].to(device)

        optimizer.zero_grad()
        logits_type, logits_severity = model(input_ids, attention_mask)

        loss = loss_fn(logits_type, labels_type) + loss_fn(logits_severity, labels_severity)
        loss.backward()
        optimizer.step()

        print(f"Loss: {loss.item():.4f}")

# ==== Inference Function ====
def predict(text):
    model.eval()
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True).to(device)

    with torch.no_grad():
        type_logits, severity_logits = model(inputs["input_ids"], inputs["attention_mask"])
        type_idx = torch.argmax(type_logits, dim=1).item()
        severity_idx = torch.argmax(severity_logits, dim=1).item()

    return {
        "type": type_labels[type_idx],
        "severity": severity_labels[severity_idx]
    }

# ==== Test Inference ====
print("\nPrediction Test:")
print(predict("My friend is unconscious, need help near CST station."))
