data_dir = "EmoBank/corpus/emobank.csv"

import pandas as pd
eb = pd.read_csv(data_dir, index_col=0)

with open("vad-train.tsv", "w") as train_file:
    with open("vad-test.tsv", "w") as test_file:
        for idx, row in eb.iterrows():
            content = row["text"] + '\t' + str(row["V"]) + ',' + str(row["A"]) + ',' + str(row["D"])
            if row["split"] == "train":
                train_file.write(content + '\n')
            else:
                test_file.write(content + '\n')
