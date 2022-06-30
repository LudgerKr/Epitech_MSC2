import pandas as pd
import turicreate as tc
from sklearn.model_selection import train_test_split

import sys

print("Load data")

transactions = pd.read_csv('./dataset/KaDo.csv', nrows=150000)
print(transactions)

print("Data preparation")
print("Create date with user, product")

data = pd.melt(transactions.set_index('CLI_ID')['LIBELLE'].apply(pd.Series).reset_index(),
             id_vars=['CLI_ID'],
             value_name='LIBELLE') \
    .dropna().drop(['variable'], axis=1) \
    .groupby(['CLI_ID', 'LIBELLE']) \
    .agg({'LIBELLE': 'count'}) \
    .rename(columns={'LIBELLE': 'purchase_count'}) \
    .reset_index() \
    .rename(columns={'LIBELLE': 'LIBELLE'})

print("Create dummy data")


def create_data_dummy(data):
    data_dummy = data.copy()
    data_dummy['purchase_dummy'] = 1
    return data_dummy


data_dummy = create_data_dummy(data)


print("Normalize item values across users")


df_matrix = pd.pivot_table(data, values='purchase_count', index='CLI_ID', columns='LIBELLE')

df_matrix_norm = (df_matrix-df_matrix.min())/(df_matrix.max()-df_matrix.min())

print(df_matrix.head())


d = df_matrix_norm.reset_index()
d.index.names = ['scaled_purchase_freq']
data_norm = pd.melt(d, id_vars=['CLI_ID'], value_name='scaled_purchase_freq')


def normalize_data(data):
    df_matrix = pd.pivot_table(data, values='purchase_count', index='CLI_ID', columns='LIBELLE')
    df_matrix_norm = (df_matrix-df_matrix.min())/(df_matrix.max()-df_matrix.min())
    d = df_matrix_norm.reset_index()
    d.index.names = ['scaled_purchase_freq']
    return pd.melt(d, id_vars=['CLI_ID'], value_name='scaled_purchase_freq').dropna()


print("Split train and test set")


def split_data(data):
    train, test = train_test_split(data, test_size=.2)
    train_data = tc.SFrame(train)
    test_data = tc.SFrame(test)
    return train_data, test_data


train_data, test_data = split_data(data)
train_data_dummy, test_data_dummy = split_data(data_dummy)
train_data_norm, test_data_norm = split_data(data_norm)


print("Define Models using Turicreate library")

# constant variables to define field names include:
user_id = 'CLI_ID'
item_id = 'LIBELLE'
users_to_recommend = list(transactions[user_id])
n_rec = 10 # number of items to recommend
n_display = 30 # to display the first few rows in an output dataset


def model(train_data, name, user_id, item_id, target, users_to_recommend, n_rec, n_display):
    if name == 'popularity':
        model = tc.popularity_recommender.create(train_data,
                                                 user_id=user_id,
                                                 item_id=item_id,
                                                 target=target)
    elif name == 'cosine':
        model = tc.item_similarity_recommender.create(train_data,
                                                      user_id=user_id,
                                                      item_id=item_id,
                                                      target=target,
                                                      similarity_type='cosine')

    elif name == 'pearson':
        model = tc.item_similarity_recommender.create(train_data,
                                                  user_id=user_id,
                                                  item_id=item_id,
                                                  target=target,
                                                  similarity_type='pearson')

    recom = model.recommend(users=users_to_recommend, k=n_rec)
    recom.print_rows(n_display)
    return model


print("Popularity model as baseline")

print("Using purchase count")
name = 'popularity'
target = 'purchase_count'
popularity = model(train_data, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)

print("Using purchase dummy")
name = 'popularity'
target = 'purchase_dummy'
pop_dummy = model(train_data_dummy, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)

print("Using scaled purchase count")
name = 'popularity'
target = 'scaled_purchase_freq'
pop_norm = model(train_data_norm, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)

print("Cosine similarity")

name = 'cosine'
target = 'purchase_count'
cos = model(train_data, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)

name = 'cosine'
target = 'purchase_dummy'
cos_dummy = model(train_data_dummy, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)

name = 'cosine'
target = 'scaled_purchase_freq'
cos_norm = model(train_data_norm, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)


print("Pearson similarity")
name = 'pearson'
target = 'purchase_count'
pear = model(train_data, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)



name = 'pearson'
target = 'purchase_dummy'
pear_dummy = model(train_data_dummy, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)



name = 'pearson'
target = 'scaled_purchase_freq'
pear_norm = model(train_data_norm, name, user_id, item_id, target, users_to_recommend, n_rec, n_display)

print("Model Evaluation")
models_w_counts = [popularity, cos, pear]
models_w_dummy = [pop_dummy, cos_dummy, pear_dummy]
models_w_norm = [pop_norm, cos_norm, pear_norm]
names_w_counts = ['Popularity Model on Purchase Counts', 'Cosine Similarity on Purchase Counts', 'Pearson Similarity on Purchase Counts']
names_w_dummy = ['Popularity Model on Purchase Dummy', 'Cosine Similarity on Purchase Dummy', 'Pearson Similarity on Purchase Dummy']
names_w_norm = ['Popularity Model on Scaled Purchase Counts', 'Cosine Similarity on Scaled Purchase Counts', 'Pearson Similarity on Scaled Purchase Counts']


eval_counts = tc.recommender.util.compare_models(test_data, models_w_counts, model_names=names_w_counts)
eval_dummy = tc.recommender.util.compare_models(test_data_dummy, models_w_dummy, model_names=names_w_dummy)
eval_norm = tc.recommender.util.compare_models(test_data_norm, models_w_norm, model_names=names_w_norm)


def create_output(model, users_to_recommend, n_rec, print_csv=True):
    recomendation = model.recommend(users=users_to_recommend, k=n_rec)
    df_rec = recomendation.to_dataframe()
    df_rec['recommendedLIBELLE'] = df_rec.groupby([user_id])[item_id] \
        .transform(lambda x: '|'.join(x.astype(str)))

    new = df_rec['recommendedLIBELLE'].str.split("|", n = 10, expand = True)
    df_rec['recommendedLIBELLE1'] = new[0]
    df_rec['recommendedLIBELLE2'] = new[1]
    df_rec['recommendedLIBELLE3'] = new[2]
    df_rec['recommendedLIBELLE4'] = new[3]
    df_rec['recommendedLIBELLE5'] = new[4]
    df_rec['recommendedLIBELLE6'] = new[5]
    df_rec['recommendedLIBELLE7'] = new[6]
    df_rec['recommendedLIBELLE8'] = new[7]
    df_rec['recommendedLIBELLE9'] = new[8]
    df_rec['recommendedLIBELLE10'] = new[9]

    df_output = df_rec[['CLI_ID', 'recommendedLIBELLE1', 'recommendedLIBELLE2',
    'recommendedLIBELLE3', 'recommendedLIBELLE4', 'recommendedLIBELLE5', 'recommendedLIBELLE6',
    'recommendedLIBELLE7', 'recommendedLIBELLE8', 'recommendedLIBELLE9', 'recommendedLIBELLE10']].drop_duplicates() \
        .sort_values('CLI_ID').set_index('CLI_ID')
    if print_csv:
        df_output.to_csv('./recommendation.csv')
        print("An output file can be found in folder with name 'recommendation.csv'")
    return df_output


df_output = create_output(pear_norm, users_to_recommend, n_rec, print_csv=True)


def customer_recomendation(customer_id):
    print(customer_id)
    if customer_id not in df_output.index:
        print('Customer not found.')
        return customer_id
    print(df_output.loc[customer_id])
    return df_output.loc[customer_id]


customer_recomendation(int(sys.argv[1]))
