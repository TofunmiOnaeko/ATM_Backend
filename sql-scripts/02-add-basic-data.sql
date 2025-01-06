USE MyDatabase;
GO

INSERT INTO Users (UserId, UserName, UserDOB, Email, Password, LatestLogin)
VALUES ('000000001A', 'tofunmiOnaeko', '2004-10-18', 'tof.onaeko@hotmail.co.uk', 'passw0rd!', '2025-01-02T14:30:00.0000000');

INSERT INTO Balance (UserId, Balance, LastUpdated)
VALUES ('000000001A', 100000, '2024-11-30');