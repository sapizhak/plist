CREATE TABLE IF NOT EXISTS articles                                                                 (
    id          TEXT    CONSTRAINT articles_id_non_empty          CHECK (id         <> '') NOT NULL ,
    product_id  TEXT    CONSTRAINT articles_product_id_non_empty  CHECK (product_id <> '') NOT NULL ,
    name        TEXT    CONSTRAINT articles_name_non_empty        CHECK (name       <> '') NOT NULL ,
    description TEXT    CONSTRAINT articles_description_non_empty CHECK (name       <> '')          ,
    price       FLOAT   CONSTRAINT articles_price_positive        CHECK (price      >  0 ) NOT NULL ,
    stock       INTEGER CONSTRAINT articles_stock_non_negative    CHECK (stock      >= 0 ) NOT NULL ,
    UNIQUE (id, product_id)
);