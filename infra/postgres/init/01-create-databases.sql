-- CampusConnect 360 — Creación de una base de datos por servicio.
-- Se ejecuta automáticamente la primera vez que arranca el contenedor postgres
-- (docker-entrypoint-initdb.d). Aísla los datos de cada microservicio.

CREATE DATABASE academic_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
CREATE DATABASE attendance_db;
CREATE DATABASE analytics_db;

-- Nota de arquitectura:
-- Usamos una instancia de PostgreSQL con 5 bases separadas para el entorno de
-- demostración (aislamiento lógico + laptop-friendly). En producción cada
-- servicio tendría su propia instancia. Ningún servicio accede a la BD de otro:
-- la integración es por API o por eventos (RabbitMQ).
