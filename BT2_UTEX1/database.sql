IF DB_ID(N'BT2_UTEX1') IS NULL
BEGIN
    CREATE DATABASE BT2_UTEX1;
END
GO

USE BT2_UTEX1;
GO

IF OBJECT_ID(N'dbo.[User]', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.[User];
END
GO

CREATE TABLE dbo.[User]
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL,
    username NVARCHAR(100) NOT NULL,
    fullname NVARCHAR(255) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    avatar NVARCHAR(255) NULL,
    roleid INT NOT NULL,
    phone NVARCHAR(20) NULL,
    createdDate DATE NOT NULL,
    CONSTRAINT UQ_User_Email UNIQUE (email),
    CONSTRAINT UQ_User_Username UNIQUE (username),
    CONSTRAINT UQ_User_Phone UNIQUE (phone)
);
GO

INSERT INTO dbo.[User] (email, username, fullname, password, avatar, roleid, phone, createdDate)
VALUES
    (N'admin@demo.com', N'admin', N'Administrator', N'123', NULL, 1, N'0900000001', CAST(GETDATE() AS DATE)),
    (N'manager@demo.com', N'manager', N'Manager', N'123', NULL, 2, N'0900000002', CAST(GETDATE() AS DATE)),
    (N'user@demo.com', N'user', N'Normal User', N'123', NULL, 5, N'0900000003', CAST(GETDATE() AS DATE));
GO
