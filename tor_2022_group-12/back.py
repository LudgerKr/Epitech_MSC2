from flask import Flask, jsonify, request
from flask_cors import CORS
from intent_classifier import naive_algo, predict
import spacy
from travel import getData, getTravel
from extract_locations import analyseSentence, locationsOrdered
from dotenv import load_dotenv
import os
import logging

app = Flask(__name__)

# initialisation de deux modèle, un pour le fr et un pour l'en
clf_fr,count_vect_fr = naive_algo(1)
clf_en,count_vect_en = naive_algo(2)

nlp = spacy.load("fr_core_news_md")

#récupération des données du dataset timetables
dataController = getData()

# Call du front avec en parametre : la phrase enregistré via le micro avec comme clé la langue selectionnée
@app.route('/travel', methods=['POST'])
def findTravel():
    intent = ""
    sentence = ""
    # récuperation de la phrase et de la langue
    request_data = request.get_json()
    if "fr" in request_data:
        sentence = request.get_json()["fr"]
        # prédiction de l'intention avec le modèle fr entrainé au lancement du serveur
        intent = predict(request.get_json()["fr"], clf_fr, count_vect_fr)    
    if "en" in request_data:
        sentence = request.get_json()["en"]
        # prédiction de l'intention avec le modèle en entrainé au lancement du serveur
        intent = predict(request.get_json()["en"], clf_en, count_vect_en)  
    if intent == "travel":
        # traîtement de la phrase afin de définir le départ et la destination
        analyse = analyseSentence(sentence, nlp)
        print(analyse)
        locationsOrder = locationsOrdered(analyse)
        # récupération des trajets correspondant au départ et à l'arrivée
        travels = getTravel(dataController, locationsOrder)
        print(travels)
    else :
        travels = "Invalid query"
    return jsonify(travels)

def prod():
    load_dotenv()
    PORT = os.getenv("PORT")
    gunicorn_error_logger = logging.getLogger("travel_order.log")
    CORS(app, resources={r"/*": {"origins": "*"}})
    app.logger.handlers.extend(gunicorn_error_logger.handlers)
    app.logger.setLevel(logging.DEBUG)
    app.run(host="0.0.0.0", port=PORT)


if __name__ == '__main__':
    app.run(debug=True)
    prod()