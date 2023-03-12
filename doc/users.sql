USE [RailwayStation]
GO

INSERT INTO [dbo].[User]
           ([login]
           ,[password]
           ,[role])
     VALUES
           ('admin', HASHBYTES('SHA2_256', N'123456') ,0)
GO

INSERT INTO [dbo].[User]
           ([login]
           ,[password]
           ,[role])
     VALUES
           ('ivanov', HASHBYTES('SHA2_256', N'ivanov') ,1)
GO

INSERT INTO [dbo].[User]
           ([login]
           ,[password]
           ,[role])
     VALUES
           ('petrov', HASHBYTES('SHA2_256', N'petrov') ,2)
GO

INSERT INTO [dbo].[User]
           ([login]
           ,[password]
           ,[role])
     VALUES
           ('sidorov', HASHBYTES('SHA2_256', N'sidorov') ,3)
GO

