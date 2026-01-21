# bigdata_beadando2.py
# Gomba osztályozó Python projekt – KNN modellel

import pandas as pd
import numpy as np
from sklearn import neighbors, metrics, preprocessing
from sklearn.model_selection import train_test_split

# ==============================
# Beállítások (fájlnevek)
# ==============================
INPUT_FILE = 'input.csv'
VALIDATION_FILE = 'validation.csv'
OUTPUT_FILE = 'pred.txt'

# ==============================
# Előfeldolgozás
# ==============================
def preprocess(df):
    """Hiányzó adatok kezelése és kategóriák numerikussá alakítása"""
    # 'cap-diameter' nélküli sorok törlése
    df.dropna(subset=['cap-diameter'], inplace=True)
    
    # 'ring-type' kitöltése leggyakoribb értékkel
    most_frequent_ring_type = df['ring-type'].value_counts().idxmax()
    df['ring-type'] = df['ring-type'].fillna(most_frequent_ring_type)
    
    # Címkék binarizálása
    df['class'] = df['class'].apply(lambda x: x == 'p')
    df['has-ring'] = df['has-ring'].apply(lambda x: x == 't')
    
    # Ordinal encoding a kategóriákhoz
    enc1 = preprocessing.OrdinalEncoder()
    enc2 = preprocessing.OrdinalEncoder()
    enc3 = preprocessing.OrdinalEncoder()
    enc4 = preprocessing.OrdinalEncoder()
    
    df['cap-shape'] = enc1.fit_transform(df[['cap-shape']])
    df['cap-color'] = enc2.fit_transform(df[['cap-color']])
    df['ring-type'] = enc3.fit_transform(df[['ring-type']])
    df['has-ring'] = enc4.fit_transform(df[['has-ring']])
    
    return df, enc1, enc2, enc3, enc4

# ==============================
# KNN modell létrehozása és pontosság vizsgálata
# ==============================
def train_knn(x_train, y_train, x_test, y_test, k_range=range(1, 10)):
    """KNN modellek létrehozása különböző k értékekkel, visszaadja a legjobbat"""
    acc_array = []
    for k in k_range:
        clf = neighbors.KNeighborsClassifier(
            n_neighbors=k, algorithm='auto', weights='distance', metric='minkowski', p=1
        )
        clf.fit(x_train, y_train)
        prediction = clf.predict(x_test)
        acc = metrics.accuracy_score(y_test.values, prediction)
        acc_array.append((clf, acc))
        print(f'k={k}, accuracy={acc:.4f}')
    
    max_val = max(acc_array, key=lambda x: x[1])
    max_index = acc_array.index(max_val)
    print(f'Max accuracy: {max_val[1]:.4f} (k={max_index + 1})')
    
    return max_val[0]  # visszaadja a legjobb KNN modellt

# ==============================
# Main
# ==============================
if __name__ == "__main__":
    # Betöltés és előfeldolgozás
    df, enc1, enc2, enc3, enc4 = preprocess(pd.read_csv(INPUT_FILE))
    
    feature_cols = ['cap-diameter', 'stem-height', 'stem-width', 'cap-shape', 
                    'cap-color', 'ring-type', 'has-ring']
    label_col = 'class'
    
    X = df[feature_cols].copy()
    y = df[label_col].copy()
    
    # Tanító / teszt felosztás
    x_train, x_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # Modell betanítása
    knn_model = train_knn(x_train, y_train, x_test, y_test)
    
    # Validációs adatok betöltése és előfeldolgozása
    vf = pd.read_csv(VALIDATION_FILE)
    
    # Hiányzó értékek kezelése és kategóriák átalakítása a már tanult encoderekkel
    vf.dropna(subset=['cap-diameter'], inplace=True)
    most_frequent_ring_type = vf['ring-type'].value_counts().idxmax()
    vf['ring-type'] = vf['ring-type'].fillna(most_frequent_ring_type)
    
    vf['class'] = vf['class'].apply(lambda x: x == 'p') if 'class' in vf.columns else None
    vf['has-ring'] = vf['has-ring'].apply(lambda x: x == 't')
    
    vf['cap-shape'] = enc1.transform(vf[['cap-shape']])
    vf['cap-color'] = enc2.transform(vf[['cap-color']])
    vf['ring-type'] = enc3.transform(vf[['ring-type']])
    vf['has-ring'] = enc4.transform(vf[['has-ring']])
    
    # Predikciók
    X_val = vf[feature_cols].copy()
    predictions = knn_model.predict(X_val)
    
    # Visszaalakítás 'p' / 'e' címkékre
    predictions_str = np.array(['p' if x else 'e' for x in predictions])
    
    # Mentés fájlba
    np.savetxt(OUTPUT_FILE, predictions_str, fmt='%s')
    print(f'Predikciók mentve: {OUTPUT_FILE}')
