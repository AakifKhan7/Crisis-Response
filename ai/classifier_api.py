import torch
from torchvision import transforms, models
from PIL import Image
from flask import Flask, request, jsonify
import io
from transformers import pipeline

from transformers import pipeline


# ===== 1. Setup =====
app = Flask(__name__)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# Your class names (same order as training)
class_names = ['Damaged_Infrastructure', 'Fire_Disaster', 'Human_Damage',
               'Land_Disaster', 'Non_Damage', 'Water_Disaster']

nlp_classifier = pipeline(
    "zero-shot-classification",
    model="facebook/bart-large-mnli",
    device=0 if torch.cuda.is_available() else -1
)

# Candidate categories
CATEGORIES = ["Medical", "Rescue", "Shelter", "Food"]

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


# ===== 6. NLP Prediction Function =====
def predict_text(text: str):
    result = nlp_classifier(text, candidate_labels=CATEGORIES)
    top_label = result["labels"][0]
    top_score = round(result["scores"][0] * 100, 2)
    return top_label, top_score


# ===== 7. Flask API route =====
@app.route('/predict/image', methods=['POST'])
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
    
    

@app.route('/predict/text', methods=['POST'])
def predict_text_api():
    data = request.get_json()
    if not data or "text" not in data:
        return jsonify({"error": "No text provided"}), 400
    
    try:
        text = data["text"]
        pred_class, confidence = predict_text(text)
        return jsonify({
            "prediction": pred_class,
            "confidence": confidence
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500


# ===== 6. Run the server =====
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
