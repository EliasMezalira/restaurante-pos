--liquibase formatted sql

--changeset restaurante:001-create-usuario
CREATE TABLE IF NOT EXISTS usuario (
                                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                       nome VARCHAR(150) NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_usuario_login'
    ) THEN
ALTER TABLE usuario
    ADD CONSTRAINT uk_usuario_login UNIQUE (login);
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_usuario_login
    ON usuario(login);



--changeset restaurante:002-create-garcom
CREATE TABLE IF NOT EXISTS garcom (
                                      id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                      nome VARCHAR(150) NOT NULL,
    percentual_gorjeta DECIMAL(5,2) NOT NULL,
    idade INTEGER NOT NULL
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_garcom_percentual'
    ) THEN
ALTER TABLE garcom
    ADD CONSTRAINT chk_garcom_percentual
        CHECK (percentual_gorjeta >= 0);
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_garcom_idade'
    ) THEN
ALTER TABLE garcom
    ADD CONSTRAINT chk_garcom_idade
        CHECK (idade > 0);
END IF;
END $$;



--changeset restaurante:003-create-menu
CREATE TABLE IF NOT EXISTS menu (
                                    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                    nome VARCHAR(150) NOT NULL,
    ingredientes TEXT,
    categoria VARCHAR(100) NOT NULL,
    valor DECIMAL(10,2) NOT NULL
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_menu_valor'
    ) THEN
ALTER TABLE menu
    ADD CONSTRAINT chk_menu_valor
        CHECK (valor > 0);
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_menu_categoria
    ON menu(categoria);



--changeset restaurante:004-create-comanda
CREATE TABLE IF NOT EXISTS comanda (
                                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                       numero_comanda VARCHAR(50) NOT NULL,
    mesa VARCHAR(20) NOT NULL,
    cliente VARCHAR(150),
    garcom_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    data_abertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fechamento TIMESTAMP NULL
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_comanda_numero'
    ) THEN
ALTER TABLE comanda
    ADD CONSTRAINT uk_comanda_numero
        UNIQUE (numero_comanda);
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_comanda_garcom'
    ) THEN
ALTER TABLE comanda
    ADD CONSTRAINT fk_comanda_garcom
        FOREIGN KEY (garcom_id)
            REFERENCES garcom(id)
            ON DELETE RESTRICT;
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_comanda_status'
    ) THEN
ALTER TABLE comanda
    ADD CONSTRAINT chk_comanda_status
        CHECK (status IN ('ABERTA', 'FECHADA', 'CANCELADA'));
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_comanda_numero
    ON comanda(numero_comanda);

CREATE INDEX IF NOT EXISTS idx_comanda_garcom
    ON comanda(garcom_id);



--changeset restaurante:005-create-comanda-item
CREATE TABLE IF NOT EXISTS comanda_item (
                                            id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                            comanda_id BIGINT NOT NULL,
                                            menu_id BIGINT NOT NULL,
                                            quantidade INTEGER NOT NULL,
                                            valor_unitario DECIMAL(10,2) NOT NULL,
    item_entregue BOOLEAN NOT NULL DEFAULT FALSE,
    observacao TEXT
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_comanda_item_comanda'
    ) THEN
ALTER TABLE comanda_item
    ADD CONSTRAINT fk_comanda_item_comanda
        FOREIGN KEY (comanda_id)
            REFERENCES comanda(id)
            ON DELETE CASCADE;
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_comanda_item_menu'
    ) THEN
ALTER TABLE comanda_item
    ADD CONSTRAINT fk_comanda_item_menu
        FOREIGN KEY (menu_id)
            REFERENCES menu(id)
            ON DELETE RESTRICT;
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_comanda_item_quantidade'
    ) THEN
ALTER TABLE comanda_item
    ADD CONSTRAINT chk_comanda_item_quantidade
        CHECK (quantidade > 0);
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_comanda_item_valor'
    ) THEN
ALTER TABLE comanda_item
    ADD CONSTRAINT chk_comanda_item_valor
        CHECK (valor_unitario > 0);
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_comanda_item_comanda
    ON comanda_item(comanda_id);

CREATE INDEX IF NOT EXISTS idx_comanda_item_menu
    ON comanda_item(menu_id);