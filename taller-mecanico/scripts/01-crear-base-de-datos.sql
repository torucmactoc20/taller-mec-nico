-- Ejecutar en SQL Server Management Studio (SSMS) conectado como administrador (sa o Windows).
-- Abrir este archivo en una "Nueva consulta" y presionar Ejecutar (F5).
--
-- Crea la base TallerMecanico y el usuario "taller" que usa la aplicacion.
-- Las tablas (CLIENTE, VEHICULO, ORDENDETRABAJO) las crea DataNucleus solas
-- la primera vez que se inicia la aplicacion.

USE master;
GO

IF DB_ID(N'TallerMecanico') IS NULL
    CREATE DATABASE TallerMecanico;
GO

IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'taller')
    CREATE LOGIN taller WITH PASSWORD = N'Taller_2026!', CHECK_POLICY = OFF;
GO

USE TallerMecanico;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'taller')
    CREATE USER taller FOR LOGIN taller;
GO

-- db_owner permite que DataNucleus cree las tablas
ALTER ROLE db_owner ADD MEMBER taller;
GO
