--liquibase formatted sql

--changeset restaurante:001-create-usuario splitStatements:false
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


--changeset restaurante:002-create-garcom splitStatements:false
CREATE TABLE IF NOT EXISTS garcom (
                                      id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                      nome VARCHAR(150) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    foto_url TEXT,
    foto_base64 TEXT,
    folgas VARCHAR(3)[] NOT NULL DEFAULT '{}'
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_garcom_documento'
    ) THEN
ALTER TABLE garcom
    ADD CONSTRAINT uk_garcom_documento UNIQUE (documento);
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_garcom_documento
    ON garcom(documento);


--changeset restaurante:003-create-cliente splitStatements:false
CREATE TABLE IF NOT EXISTS cliente (
                                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                       nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(11) NOT NULL,
    rua VARCHAR(200) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(200),
    bairro VARCHAR(100) NOT NULL,
    referencia VARCHAR(255),
    email VARCHAR(150),
    observacoes TEXT
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_cliente_telefone'
    ) THEN
ALTER TABLE cliente
    ADD CONSTRAINT chk_cliente_telefone
        CHECK (telefone ~ '^[0-9]{10,11}$');
END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_cliente_nome
    ON cliente(nome);

CREATE INDEX IF NOT EXISTS idx_cliente_telefone
    ON cliente(telefone);


--changeset restaurante:004-create-menu splitStatements:false
CREATE TABLE IF NOT EXISTS menu (
                                    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                    categoria VARCHAR(30) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    ingredientes TEXT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    imagem_url TEXT,
    imagem_base64 TEXT
    );

DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_menu_categoria'
    ) THEN
ALTER TABLE menu
    ADD CONSTRAINT chk_menu_categoria
        CHECK (
            categoria IN (
                          'ENTRADA',
                          'PRATO_PRINCIPAL',
                          'SOBREMESA',
                          'BEBIDA'
                )
            );
END IF;

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


--changeset restaurante:005-create-comanda splitStatements:false
CREATE TABLE IF NOT EXISTS comanda (
                                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                       numero_comanda VARCHAR(50) NOT NULL,
    mesa VARCHAR(20) NOT NULL,
    cliente_id BIGINT,
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
        WHERE conname = 'fk_comanda_cliente'
    ) THEN
ALTER TABLE comanda
    ADD CONSTRAINT fk_comanda_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES cliente(id)
            ON DELETE SET NULL;
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
        CHECK (
            status IN (
                       'ABERTA',
                       'FECHADA',
                       'CANCELADA'
                )
            );
END IF;

END $$;

CREATE INDEX IF NOT EXISTS idx_comanda_numero
    ON comanda(numero_comanda);

CREATE INDEX IF NOT EXISTS idx_comanda_cliente
    ON comanda(cliente_id);

CREATE INDEX IF NOT EXISTS idx_comanda_garcom
    ON comanda(garcom_id);


--changeset restaurante:006-create-comanda-item splitStatements:false
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