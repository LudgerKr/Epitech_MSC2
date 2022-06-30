import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.naive_bayes import MultinomialNB

# préparation des données provenant du dataset de gestion des intentions. 
def data_prepare(lang):
  if lang == 1:
    col = ['text-fr', 'intent']
    y = pd.read_csv('dataset/test_intent_dataset.csv')
    y = y[col]
    y = y[pd.notnull(y['text-fr'])]
    # normalisation des colonnes peu importe la langue
    y.columns = ['text', 'intent']
  if lang == 2:
    col = ['text-en', 'intent']
    y = pd.read_csv('dataset/test_intent_dataset.csv')
    y = y[col]
    y = y[pd.notnull(y['text-en'])]
    # normalisation des colonnes peu importe la langue
    y.columns = ['text', 'intent'] 
  return y

def naive_algo(lang): 
  # initialisation du modèle permettant la vectorisation et la transformation du dataset 
  if lang == 1:
    tfidf = TfidfVectorizer(sublinear_tf=True, min_df=5, ngram_range=(1, 2))
  if lang == 2:
    tfidf = TfidfVectorizer(sublinear_tf=True, min_df=5, ngram_range=(1, 2), stop_words="english")
  # préparation du dataset selon la langue
  df = data_prepare(lang)
  # Vectorisation du dataset afin d'obtenir une matrice qui pourra être entraînée et servira aux prédictions
  features = tfidf.fit_transform(df.text).toarray()
  features.shape
  # Dernière altération du dataset afin de préparer une partie d'entraînement et une partie de test
  X_train, X_test, y_train, y_test = train_test_split(df['text'], df['intent'], random_state = 0)
  count_vect = CountVectorizer()
  X_train_counts = count_vect.fit_transform(X_train)
  tfidf_transformer = TfidfTransformer()
  X_train_tfidf = tfidf_transformer.fit_transform(X_train_counts)
  # Entraînement du model Naive bayes
  clf = MultinomialNB().fit(X_train_tfidf, y_train)
  return clf,count_vect

def predict(question, clf, count_vect):
  intent=clf.predict(count_vect.transform([question]))
  intent=str(intent).strip("['']")
  return intent