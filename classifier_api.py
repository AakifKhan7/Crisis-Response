import torch
from torchvision import transforms, models
from PIL import Image
from flask import Flask, request, jsonify
import io

# ===== 1. Setup =====
app = Flask(__name__)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# Your class names (same order as training)
class_names = ['Damaged_Infrastructure', 'Fire_Disaster', 'Human_Damage',
               'Land_Disaster', 'Non_Damage', 'Water_Disaster']

# ===== 2. Define same transform used for validation =====
val_transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize([0.485, 0.456, 0.406],
                         [0.229, 0.224, 0.225])
])

# ===== 3. Load trained model =====
model = models.resnet18(weights=None)
model.fc = torch.nn.Sequential(
    torch.nn.Dropout(0.5),
    torch.nn.Linear(model.fc.in_features, len(class_names))
)
model.load_state_dict(torch.load("best_disaster_model.pth", map_location=device))
model = model.to(device)
model.eval()


# ===== 4. Prediction function =====
def predict_image(image_bytes):
    img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    img_tensor = val_transform(img).unsqueeze(0).to(device)
    with torch.no_grad():
        outputs = model(img_tensor)
        probs = torch.nn.functional.softmax(outputs, dim=1)
        top_prob, top_class = probs.topk(1, dim=1)
    return class_names[top_class.item()], top_prob.item()


# ===== 5. Flask API route =====
@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({"error": "No file uploaded"}), 400
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "Empty filename"}), 400
    
    try:
        img_bytes = file.read()
        pred_class, confidence = predict_image(img_bytes)
        return jsonify({
            "prediction": pred_class,
            "confidence": round(confidence * 100, 2)
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500


# ===== 6. Run the server =====
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
