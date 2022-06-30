import spacy
from enum import Enum
import fr_core_news_md
import numpy as np
from spacy.symbols import PROPN, NOUN, CCONJ, ADP, VERB

class SetDirection(Enum):
    DEPARTURE = "DEPARTURE"
    DESTINATION = "DESTINATION"

class Word:
    def __init__(self, word: str, direction: SetDirection, score: str):
        self.word = word
        self.direction = direction
        self.score = score


CCONJ_Score = [
    # DEPARTURE
    Word("depuis", SetDirection.DEPARTURE, "STRONG"),
    # Destination
    Word("et", SetDirection.DESTINATION, "STRONG"),
    Word("puis", SetDirection.DESTINATION, "STRONG")
]


NOUN_Score = [
    # DEPARTURE
    Word("provenance", SetDirection.DEPARTURE, "STRONG"),
    # Destination
    Word("destination", SetDirection.DESTINATION, "STRONG"),
    Word("direction", SetDirection.DESTINATION, "WEAK")
]


ADP_Score = [
    # DEPARTURE
    Word("de", SetDirection.DEPARTURE, "STRONG"),
    Word("des", SetDirection.DEPARTURE, "STRONG"),
    Word("depuis", SetDirection.DEPARTURE, "STRONG"),
    Word("du", SetDirection.DEPARTURE, "STRONG"),
    # Destination 
    Word("par", SetDirection.DESTINATION, "WEAK"),
    Word("dans", SetDirection.DESTINATION, "WEAK"),
    Word("à", SetDirection.DESTINATION, "WEAK"),
    Word("au", SetDirection.DESTINATION, "WEAK"),
    Word("aux", SetDirection.DESTINATION, "WEAK")
] 

VERB_Score = [
    # DEPARTURE
    Word("décoller", SetDirection.DEPARTURE, "STRONG"),
    Word("prendre", SetDirection.DEPARTURE, "STRONG"),
    Word("être", SetDirection.DEPARTURE, "STRONG"),
    Word("après", SetDirection.DEPARTURE, "WEAK"),
    Word("de", SetDirection.DEPARTURE, "STRONG"),
    # Destination
    Word("arriver", SetDirection.DESTINATION, "STRONG"),
    Word("aller", SetDirection.DESTINATION, "STRONG"),
    Word("atterrir", SetDirection.DESTINATION, "STRONG"),
    Word("découvrir", SetDirection.DESTINATION, "STRONG"),
    Word("rendre", SetDirection.DESTINATION, "STRONG"),
    Word("visiter", SetDirection.DESTINATION, "STRONG"),
    Word("voyager", SetDirection.DESTINATION, "STRONG"),
    Word("avant", SetDirection.DESTINATION, "STRONG")
]

def analyseSentence(sentence):
    print(f"Sentence: {sentence}")
    nlp = spacy.load("fr_core_news_md")
    doc = nlp(sentence)
    locations = []
        
    # Extract locations
    locations.append([ent.text for ent in doc.ents if ent.label_ in ['LOC']])

    if len(locations[0]) < 1:
        print("Cannot parse sentence or invalid sentence.")

    index = 0
    limit = len(doc)

    locationsAndScores = []

    departureScore = 0
    destinationScore = 0

    while(index < limit):
        token = doc[index]

        if token.pos_ == 'VERB':
            for score in VERB_Score:
                if score.word == token.lemma_:
                    if(score.direction.name == "DEPARTURE" and score.score == "WEAK"):
                        departureScore += 1
                    if(score.direction.name == "DEPARTURE" and score.score == "STRONG"):
                        departureScore += 2
                    elif(score.direction.name == "DESTINATION" and score.score == "WEAK"):
                        destinationScore += 1
                    elif(score.direction.name == "DESTINATION" and score.score == "STRONG"):
                        destinationScore += 2
                    if doc[index+1].text in locations[0]:
                        locationsAndScores.append({
                            "location": doc[index+1].text,
                            "departureScore": departureScore,
                            "destinationScore": destinationScore
                        })
                        # departureScore = 0
                        # destinationScore = 0

        if token.pos_ == 'CCONJ':
            for score in CCONJ_Score:
                if score.word == token.lemma_:
                    if(score.direction.name == "DEPARTURE" and score.score == "WEAK"):
                        departureScore += 1
                    if(score.direction.name == "DEPARTURE" and score.score == "STRONG"):
                        departureScore += 2
                    elif(score.direction.name == "DESTINATION" and score.score == "WEAK"):
                        destinationScore += 1
                    elif(score.direction.name == "DESTINATION" and score.score == "STRONG"):
                        destinationScore += 2
                    if doc[index+1].text in locations[0]:
                        locationsAndScores.append({
                            "location": doc[index+1].text,
                            "departureScore": departureScore,
                            "destinationScore": destinationScore
                        })
                        ## departureScore = 0
                        ## destinationScore = 0


        if token.pos_ == 'NOUN':
            for score in NOUN_Score:
                if score.word == token.lemma_:
                    if(score.direction.name == "DEPARTURE" and score.score == "WEAK"):
                        departureScore += 1
                    if(score.direction.name == "DEPARTURE" and score.score == "STRONG"):
                        departureScore += 2
                    elif(score.direction.name == "DESTINATION" and score.score == "WEAK"):
                        destinationScore += 1
                    elif(score.direction.name == "DESTINATION" and score.score == "STRONG"):
                        destinationScore += 2
                    if doc[index+1].text in locations[0]:
                        locationsAndScores.append({
                            "location": doc[index+1].text,
                            "departureScore": departureScore,
                            "destinationScore": destinationScore
                        })
                        ## departureScore = 0
                        ## destinationScore = 0

        if token.pos_ == 'ADP':
            for score in ADP_Score:
                if score.word == token.lemma_:
                    if(score.direction.name == "DEPARTURE" and score.score == "WEAK"):
                        departureScore += 1
                    if(score.direction.name == "DEPARTURE" and score.score == "STRONG"):
                        departureScore += 2
                    elif(score.direction.name == "DESTINATION" and score.score == "WEAK"):
                        destinationScore += 1
                    elif(score.direction.name == "DESTINATION" and score.score == "STRONG"):
                        destinationScore += 2
                    if doc[index+1].text in locations[0]:
                        locationsAndScores.append({
                            "location": doc[index+1].text,
                            "departureScore": departureScore,
                            "destinationScore": destinationScore
                        })
                        ## departureScore = 0
                        ## destinationScore = 0

        index += 1

    return locationsAndScores


# Etape 1 : Departure Max - Destination Min proche de 0
# Etape 2 : Departure Max - Destination Min
# Etape 3 : Departure Min - Departure Max
def locationsOrdered(locationsAndScores):
    locationsOrdered = []
    for i in range(len(locationsAndScores)):
        if ((locationsAndScores[i]["departureScore"] >= 2) and (locationsAndScores[i]["destinationScore"] == 0)):
            locationsOrdered.append(locationsAndScores[i]["location"])
            locationsAndScores.pop(i)
            break

    if len(locationsAndScores) >= 2:
        if (locationsAndScores[0]["departureScore"]) == (locationsAndScores[1]["departureScore"]):
            if (locationsAndScores[0]["departureScore"]) > (locationsAndScores[1]["departureScore"]):
                locationsOrdered.append(locationsAndScores[1])
                locationsAndScores.pop(1)

    sorted_list = sorted(locationsAndScores, key=lambda k: (int(k['destinationScore']), -int(k["departureScore"])))

    for i in range(len(sorted_list)):
        locationsOrdered.append(sorted_list[i]["location"])

    return locationsOrdered

def testNLP(sentence):
    locationsAndScores = analyseSentence(sentence)
    toto =  locationsOrdered(locationsAndScores)
    print(toto);
    return toto;
testNLP("Je voudrais partir à Strasbourg, en partant de Paris")
