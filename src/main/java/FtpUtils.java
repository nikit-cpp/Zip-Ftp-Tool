// http://www.coderanch.com/t/513952/open-source/FTPClient-working
// possible troubles: FTP 425

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;


public class FtpUtils {

	/**
	 * LOG
	 */
	private static final Log LOG = LogFactory.getLog(FtpUtils.class);

	/**
	 * Reply code.
	 */
	private static final int REPLY_CODE = 425;

	/**
	 * Ftp server name.
	 */
	private String ftpServer;

	/**
	 * Ftp server login username.
	 */
	private String username;

	/**
	 * Ftp server login password.
	 */
	private String password;

	/**
	 * Folder name on the ftp server the files need to be put.
	 */
	private String destFolder;

	/**
	 * Secure command.
	 */
	private String umask;

	/**
	 * buffer size.
	 */
	private Integer bufferSize;

	/**
	 * Number of attempts to connect to ftp server.
	 */
	int attemptCount = 0;

	/**
	 * Default FtpUtils constructor.
	 */
	public FtpUtils() {
		super();
	}

	/**
	 * This parameter FtpUtils constructor sets the all parameters values from
	 * properties file, these values are set through constructor-arg tag in
	 * applicationContext for this class.
	 * 
	 * @param ftpServer
	 *            - Ftp server name.
	 * @param username
	 *            - Ftp server login username.
	 * @param password
	 *            - Ftp server login password.
	 * @param destFolder
	 *            - Folder name on the ftp server the files need to be put.
	 * @param umask
	 *            - Secure command.
	 */
	public FtpUtils(final String ftpServer, final String username,
			final String password, final String destFolder, final String umask,
			final Integer bufferSize) {
		super();
		this.ftpServer = ftpServer;
		this.username = username;
		this.password = password;
		this.destFolder = destFolder;
		this.umask = umask;
		this.bufferSize = bufferSize;
	}

	/**
	 * Perform FTP utilizing code from Jakarta Commons Net.
	 * 
	 * It performs FTP a single file.
	 * 
	 * @param sourceFile
	 *            file to be stored.
	 * 
	 * @throws FtpException
	 *             FTPClient command failure.
	 */
	public void performFtp(final File sourceFile, final String destFolderName)
			throws FtpException {
		FTPClient ftpClient = null;

		try {
			// Connect to FTP server
			ftpClient = connectFtpServer();
		} catch (final IOException ioe) {
			throw new FtpException(ioe.getMessage());
		}

		InputStream input = null;
		// Attempt FTP Transfer, login
		try {
			if (!ftpClient.login(username, password)) {
				if (LOG.isFatalEnabled()) {
					LOG.fatal("FTP login failed.");
				}
				ftpClient.logout();
				ftpClient.disconnect();
				throw new FtpException("FTP Login failed");
			}

			final StringTokenizer homeDirectory = new StringTokenizer(ftpClient
					.printWorkingDirectory(), File.separator);
			final int nofDirToChange = homeDirectory.countTokens();
			// Change Directory up to root folder(Parent directory) to avoid the
			// home directory.
			for (int count = 0; count < nofDirToChange + 1; count++) {
				ftpClient.changeToParentDirectory();
				if (LOG.isDebugEnabled()) {
					LOG.debug("cpd reply = " + ftpClient.getReplyString());
				}
			}

			// set file type as in binary mode
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setRemoteVerificationEnabled(false);

			// print current working directory.
			ftpClient.printWorkingDirectory();
			if (LOG.isDebugEnabled()) {
				LOG.debug("pwd reply = " + ftpClient.getReplyString());
			}

			String destinationFolder = destFolder + destFolderName;
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Upload to path - " + destinationFolder);
			}

			// Create Directory structure (if not found).
			final StringTokenizer st = new StringTokenizer(destinationFolder,
					File.separator);
			while (st.hasMoreElements()) {
				final String dir = st.nextToken();
				ftpClient.makeDirectory(dir);
				ftpClient.changeWorkingDirectory(dir);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("cwd reply = " + ftpClient.getReplyString());
			}
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				if (LOG.isWarnEnabled()) {
					LOG.warn("cwd failed, reply = "
							+ ftpClient.getReplyString());
				}
				// log off destination server
				ftpClient.logout();
				ftpClient.disconnect();
				throw new FtpException("cwd failed, reply = "
						+ ftpClient.getReplyString());
			}
			// then print current working directory after creation
			// of directory structure.
			ftpClient.printWorkingDirectory();
			if (LOG.isDebugEnabled()) {
				LOG.debug("pwd reply = " + ftpClient.getReplyString());
			}
			// then set site command
			ftpClient.sendSiteCommand("umask " + umask);
			if (LOG.isDebugEnabled()) {
				LOG.debug("umask reply = " + ftpClient.getReplyString());
			}
			// Then FTP file to destination
			input = new FileInputStream(sourceFile);
			ftpClient.setBufferSize(bufferSize.intValue());
			final long startTime = System.nanoTime();
			if (LOG.isInfoEnabled()) {
				LOG.info("Start time for sending file '"
						+ sourceFile.getName()
						+ "'  "
						+ DateFormatUtils.format(Calendar.getInstance()
								.getTime(), "yyyy-MM-dd hh:mm:ss"));
			}
			LOG.info("************BEGIN***************************:"+sourceFile.getName());
			[size=18][color=red]ftpClient.storeFile(sourceFile.getName(), input);[/color][/size]                  // --- This is not working... wait wait wait then error..................
			LOG.info("**************END***************************");
            
			LOG.info("REPLY CODE : "+ftpClient.getReplyCode());
            
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				// If "Can't build data communication",
				// try again in local passive mode
				if (ftpClient.getReplyCode() == REPLY_CODE) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("put failed, trying again in passive mode");
					}

					ftpClient.enterLocalPassiveMode();
					if (LOG.isDebugEnabled()) {
						LOG.debug("passive reply = "
								+ ftpClient.getReplyString());
					}

					ftpClient.storeFile(sourceFile.getName(), input);
					if (!FTPReply
							.isPositiveCompletion(ftpClient.getReplyCode())) {
						if (LOG.isFatalEnabled()) {
							LOG.fatal("passive put failed, reply = "
									+ ftpClient.getReplyString());
						}
						IOUtils.closeQuietly(input);
						// log off destination server
						ftpClient.logout();
						ftpClient.disconnect();
						throw new FtpException("Passive FTP failed: "
								+ ftpClient.getReplyCode());
					}
				} else {
					if (LOG.isFatalEnabled()) {
						LOG.fatal("FTP failed, reply = "
								+ ftpClient.getReplyString());
					}
					
					IOUtils.closeQuietly(input);
					// log off destination server
					ftpClient.logout();
					ftpClient.disconnect();
					throw new FtpException("FTP failed: "
							+ ftpClient.getReplyCode());
				}
			}

			if (LOG.isInfoEnabled()) {
				final long finishTime = System.nanoTime();
				LOG.info("End time for sending file '"
						+ sourceFile.getName()
						+ "' "
						+ DateFormatUtils.format(Calendar.getInstance()
								.getTime(), "yyyy-MM-dd hh:mm:ss"));
				LOG.info("'" + sourceFile.getAbsolutePath()
						+ "' uploaded successfully to '" + ftpServer + ":"
						+ destFolder + sourceFile.getName() + "' in '"
						+ (finishTime - startTime) + "' nanoseconds.");
			}
			IOUtils.closeQuietly(input);
			// log off destination server
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (final FTPConnectionClosedException ftpcce) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Server closed connection prematurely.");
			}
			throw new FtpException("Server closed connection prematurely. "
					+ ftpcce.getMessage());
		} catch (final IOException ioe) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("IOException occurred: " + ioe.getMessage());
			}
			throw new FtpException(ioe.getMessage());
		} finally {
			IOUtils.closeQuietly(input);
			// disconnect from destination site
			while (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (final IOException ioex) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("Problem, while disconnecting to FTP server '"
								+ ftpServer + "'");
					}
					throw new FtpException(
							"Problem, while disconnecting to FTP server. "
									+ ioex.getMessage());
				}
			}
		}
	}

	/**
	 * Perform FTP multiple files to the server.
	 * 
	 * @param sourceFileList
	 *            list of files to be stored.
	 * @throws FtpException
	 *             FTPClient command failure.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void ftpMultipleFiles(final List sourceFileList) throws FtpException {
		FTPClient ftp = null;
		try {
			ftp = connectFtpServer();
		} catch (final IOException ioe) {
			throw new FtpException(ioe.getMessage());
		}

		// Attempt FTP Transfer
		try {
			if (!ftp.login(username, password)) {
				if (LOG.isFatalEnabled()) {
					LOG.fatal("FTP login failed");
				}
				ftp.logout();
				ftp.disconnect();
				throw new FtpException("FTP login failed.");
			}

			// upload file to destination
			ftp.printWorkingDirectory();
			if (LOG.isDebugEnabled()) {
				LOG.debug("pwd = " + ftp.getReplyCode());
			}

			ftp.changeToParentDirectory();
			if (LOG.isDebugEnabled()) {
				LOG.debug("change to parent dir = " + ftp.getReplyCode());
			}

			final StringTokenizer st = new StringTokenizer(destFolder, ""
					+ File.separatorChar);
			while (st.hasMoreElements()) {
				final String dir = st.nextToken();
				ftp.makeDirectory(dir);
				ftp.changeWorkingDirectory(dir);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("cwd reply = " + ftp.getReplyString());
			}
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("cwd failed, reply = " + ftp.getReplyString());
				}
				ftp.logout();
				ftp.disconnect();
				throw new FtpException("cwd failed, reply = "
						+ ftp.getReplyString());
			}
			ftp.printWorkingDirectory();
			if (LOG.isDebugEnabled()) {
				LOG.debug("pwd = " + ftp.getReplyCode());
			}

			ftp.sendSiteCommand("umask " + umask);
			if (LOG.isDebugEnabled()) {
				LOG.debug("umask reply = " + ftp.getReplyString());
			}
			InputStream input = null;
			for (int i = 0; i < sourceFileList.size(); i++) {
				final String sourceFile = (String) sourceFileList.get(i);
				input = new FileInputStream(sourceFile);
				String destFilename;
				final int index = sourceFile.lastIndexOf(File.separator);
				// Destination filename is the source filename without any
				// folder path
				if (index == -1) {
					// No folder paths
					destFilename = sourceFile;
				} else {
					// Remove folder paths
					destFilename = sourceFile.substring(index + 1, sourceFile
							.length());
				}
				// Put the file
				ftp.storeFile(destFilename, input);
				if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					if (LOG.isInfoEnabled()) {
						LOG.info(destFilename + " uploaded successfully to "
								+ ftpServer + ":" + destFolder
								+ File.separatorChar + destFilename);
					}
				} else {
					if (LOG.isFatalEnabled()) {
						LOG
								.fatal("put failed, reply = "
										+ ftp.getReplyString());
					}
				}
				IOUtils.closeQuietly(input);
				input = null;
			}
			// log off destination server
			ftp.logout();
			ftp.disconnect();
		} catch (final FTPConnectionClosedException ftpe) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Server closed connection prematurely.");
			}
			throw new FtpException("Server closed connection prematurely. "
					+ ftpe.getMessage());
		} catch (final IOException ioe) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("IOException during data transfer");
			}
			throw new FtpException("IOException during data transfer. "
					+ ioe.getMessage());
		} finally {
			// disconnect from destination site
			while (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (final IOException f) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("Problem, while disconnecting to FTP server '"
								+ ftpServer + "'");
					}
					throw new FtpException(
							"Problem, while disconnecting to FTP server."
									+ f.getMessage());
				}
			}
		}
	}

	/**
	 * Connects FTP server.
	 * 
	 * @return connected FTPClient object.
	 * @throws IOException
	 *             - if not connected to FTP.
	 */
	private FTPClient connectFtpServer() throws IOException {
		
		LOG.info("Begin  connectFtpServer: ");
		
		LOG.info("ftpServer : "+ftpServer);
		LOG.info("username : "+username);
		LOG.info("password : "+password);
		LOG.info("destFolder : "+destFolder);
		LOG.info("umask : "+umask);
		LOG.info("bufferSize : "+bufferSize);
		
		final FTPClient ftp = new FTPClient();
		try {
			Validate.notNull(ftpServer, "FTP Server could not be null.");
			// Attempt to connect to destination site
			ftp.connect(ftpServer);

			// Check for FTP connection error
			if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Connected to '" + ftpServer + "'");
				}
			} else {
				if (LOG.isFatalEnabled()) {
					LOG.fatal("Connect reply = " + ftp.getReplyString());
				}
				ftp.disconnect();
				throw new IOException("FTP connect failed : "
						+ ftp.getReplyCode());
			}
		} catch (final IOException e) {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (final IOException f) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("Disconnect from server '" + ftpServer
								+ "' failed.");
					}
					throw e;
				}
			}
			if (LOG.isFatalEnabled()) {
				LOG.fatal("Could not connect to server '" + ftpServer + "' ("
						+ e.getMessage() + ")");
				// Here try looping and calling 5 times..
				attemptCount++;
				if (attemptCount < 6) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Attempt count ->" + attemptCount);
					}
					connectFtpServer();
				}
			}
			throw e;
		}
		LOG.info("END  connectFtpServer: ");
		return ftp;
	}

	/**
	 * Get the list of the file names to FTP all the files.
	 * 
	 * @param String
	 *            parentFolder To get all the file path and name.
	 * @return List<String> Which contains the file full path and name string.
	 */
	public List<String> getFileList(String parentFolder) {
		final List<String> fileList = new ArrayList<String>();
		final File folder = new File(parentFolder);
		final File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileList.add(listOfFiles[i].getAbsolutePath());
			}
		}
		return fileList;

	}

	/**
	 * Get the list of the folder names to write the files.
	 * 
	 * @param String
	 *            parentFolder To get all the file path and name.
	 * @return List<File> Which contains the file full path and name string.
	 */
	public List<File> getFolderList(String parentFolder) {
		final List<File> folderList = new ArrayList<File>();
		final File folder = new File(parentFolder);
		final File[] listOfFolders = folder.listFiles();

		for (int i = 0; i < listOfFolders.length; i++) {
			if (listOfFolders[i].isDirectory()) {
				folderList.add(listOfFolders[i]);
			}
		}
		return folderList;

	}
}

  