import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.command.InvocationRecord;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.stub.command.AbstractStorCommandHandler;
import org.mockftpserver.stub.command.StorCommandHandler;

public class DelayedAfterUploadCommandHandler extends StorCommandHandler {
	protected void afterProcessData(Command command, Session session, InvocationRecord invocationRecord) throws Exception {
		System.out.println("Привет, я - DelayedAfterUploadCommandHandler, и сейчас я засну на 4 секунды...");
        Thread.sleep(4000);
        System.out.println("проснулся");
        FtpUploaderTest.getUpdateLatch().countDown();
    }
}
