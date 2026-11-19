-- Ejecutar una sola vez en SQL Server Management Studio, conectado como administrador.
-- Crea la base TallerMecanico y el usuario "taller" (los mismos valores por defecto de application.properties).
-- Las TABLAS las crea DataNucleus automáticamente al iniciar la aplicación.

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

-- db_owner permite que DataNucleus cree las tablas al arrancar.
ALTER ROLE db_owner ADD MEMBER taller;
GO
