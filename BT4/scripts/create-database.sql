-- Run once using SQL Server Management Studio or sqlcmd.
-- Existing databases and data are preserved.
IF DB_ID(N'BT4') IS NULL
    CREATE DATABASE [BT4];
GO
