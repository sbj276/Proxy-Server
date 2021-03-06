USE [ProxyServer]
GO
/****** Object:  Table [dbo].[UserActivityLog]    Script Date: 02/12/2015 01:29:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserActivityLog](
	[PersonId] [nvarchar](20) NOT NULL,
	[SessionId] [nvarchar](500) NOT NULL,
	[RemoteAddress] [nvarchar](50) NOT NULL,
	[RemotePort] [nvarchar](50) NOT NULL,
	[HttpMethod] [nvarchar](50) NULL,
	[RemoteHost] [nvarchar](500) NULL,
	[Scheme] [nvarchar](50) NOT NULL,
	[RequestURL] [nvarchar](4000) NOT NULL,
	[QueryString] [nvarchar](max) NULL,
	[IsAllowed] [nchar](10) NOT NULL,
	[TimeOfRequest] [datetime] NOT NULL,
	[ActualURL] [nvarchar](4000) NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SequenceTable]    Script Date: 02/12/2015 01:29:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SequenceTable](
	[SequenceName] [nvarchar](50) NOT NULL,
	[CurrentSequenceNumber] [numeric](18, 0) NOT NULL,
	[MaxAllowedSequenceNumber] [numeric](18, 0) NOT NULL,
 CONSTRAINT [PK_SequenceTable] PRIMARY KEY CLUSTERED 
(
	[SequenceName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PersonMaster]    Script Date: 02/12/2015 01:29:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PersonMaster](
	[TOPId] [int] NOT NULL,
	[TOPName] [nvarchar](50) NOT NULL,
	[TOPDesc] [nvarchar](50) NULL,
 CONSTRAINT [PK_PersonMaster] PRIMARY KEY CLUSTERED 
(
	[TOPId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BlockedURLSForUsers]    Script Date: 02/12/2015 01:29:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BlockedURLSForUsers](
	[PersonId] [nvarchar](20) NOT NULL,
	[BlockedURL] [nvarchar](1000) NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Login]    Script Date: 02/12/2015 01:29:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Login](
	[UserName] [nvarchar](20) NOT NULL,
	[Password] [nvarchar](30) NOT NULL,
	[PersonId] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_Login] PRIMARY KEY CLUSTERED 
(
	[UserName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Person]    Script Date: 02/12/2015 01:29:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Person](
	[PersonId] [nvarchar](20) NOT NULL,
	[TOPId] [int] NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Address] [nvarchar](1000) NOT NULL,
	[BGroup] [nchar](10) NOT NULL,
	[DOB] [int] NOT NULL,
	[ContactNo] [nchar](15) NOT NULL,
	[EMailId] [nvarchar](50) NOT NULL,
	[Gender] [nchar](6) NOT NULL,
	[IsRemoved] [nchar](10) NOT NULL,
 CONSTRAINT [PK_Person] PRIMARY KEY CLUSTERED 
(
	[PersonId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  ForeignKey [FK_BlockedURLSForUsers_Person]    Script Date: 02/12/2015 01:29:18 ******/
ALTER TABLE [dbo].[BlockedURLSForUsers]  WITH CHECK ADD  CONSTRAINT [FK_BlockedURLSForUsers_Person] FOREIGN KEY([PersonId])
REFERENCES [dbo].[Person] ([PersonId])
GO
ALTER TABLE [dbo].[BlockedURLSForUsers] CHECK CONSTRAINT [FK_BlockedURLSForUsers_Person]
GO
/****** Object:  ForeignKey [FK_Login_Person]    Script Date: 02/12/2015 01:29:18 ******/
ALTER TABLE [dbo].[Login]  WITH CHECK ADD  CONSTRAINT [FK_Login_Person] FOREIGN KEY([PersonId])
REFERENCES [dbo].[Person] ([PersonId])
ON UPDATE CASCADE
GO
ALTER TABLE [dbo].[Login] CHECK CONSTRAINT [FK_Login_Person]
GO
/****** Object:  ForeignKey [FK_Person_PersonMaster]    Script Date: 02/12/2015 01:29:18 ******/
ALTER TABLE [dbo].[Person]  WITH CHECK ADD  CONSTRAINT [FK_Person_PersonMaster] FOREIGN KEY([TOPId])
REFERENCES [dbo].[PersonMaster] ([TOPId])
ON UPDATE CASCADE
GO
ALTER TABLE [dbo].[Person] CHECK CONSTRAINT [FK_Person_PersonMaster]
GO
