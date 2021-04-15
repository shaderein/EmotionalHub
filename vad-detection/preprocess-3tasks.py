data_dir = "EmoBank/corpus/emobank.csv"

import pandas as pd
eb = pd.read_csv(data_dir, index_col=0)

with open("valence-train.tsv", "w") as train_v_file, open("valence-test.tsv", "w") as test_v_file:
    with open("arousal-train.tsv", "w") as train_a_file, open("arousal-test.tsv", "w") as test_a_file:
        with open("dominance-train.tsv", "w") as train_d_file, open("dominance-test.tsv", "w") as test_d_file:
            for idx, row in eb.iterrows():
                if row["split"] == "train":
                    train_v_file.write(row["text"] + '\t' + str(row["V"]) + '\n')
                    train_a_file.write(row["text"] + '\t' + str(row["A"]) + '\n')
                    train_d_file.write(row["text"] + '\t' + str(row["D"]) + '\n')
                else:
                    test_v_file.write(row["text"] + '\t' + str(row["V"]) + '\n')
                    test_a_file.write(row["text"] + '\t' + str(row["A"]) + '\n')
                    test_d_file.write(row["text"] + '\t' + str(row["D"]) + '\n')
