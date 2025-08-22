-- Este script será executado apenas na base de dados H2 para criar a tabela de simulações.
CREATE TABLE IF NOT EXISTS SIMULACAO (
    id_simulacao INT AUTO_INCREMENT PRIMARY KEY,
    valor_desejado DECIMAL(20, 2) NOT NULL,
    prazo INT NOT NULL,
    resultado_json CLOB NOT NULL
);