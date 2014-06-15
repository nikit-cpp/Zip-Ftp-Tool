package main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import controller.Controller;
import controller.Event;
import controller.Event.Events;
import session.Session;

public class Starter implements Runnable {
	public static final String lookupFolder_ = Config.getInstance()
			.getLookupFoder();
	public static final String destFolder_ = Config.getInstance()
			.getDestFoder();
	public static final String ftpFolder_ = Config.getInstance().getFtpFoder(); // "/public_html"

	public Starter() {
	}

	public static void main(String[] args) {
		new Starter().run();
	}

	public void run() {
		System.out.println("HELLO.");
		// System.in.read();

		final File lookupFolder = new File(lookupFolder_);

		System.out.println("Проверка просматриваемой папки...");
		System.out.println("Путь: " + lookupFolder.getAbsolutePath());
		System.out.println("Существует? " + lookupFolder.exists());
		System.out.println("Папка? " + lookupFolder.isDirectory());

		// Архивируем
		Zipper zipper = new Zipper();
		System.out.println("Папка расположения zip-архивов: " + destFolder_);
		for (File zippableFolder : lookupFolder.listFiles()) {
			if (zippableFolder.isDirectory()) {
				System.out.println("Папка-Кандидат на запаковку: "
						+ zippableFolder.getAbsolutePath());
				zipper.zip(zippableFolder, destFolder_);
			}
		}

		// create new filename filter
		final FilenameFilter zipfiles = new ZipFilenameFilter();

		// Отдельная сессия на каждый сервер
		Session[] sessions = Config.getInstance().createFtpUploaderArray();

		// Создаём массив потоков
		Thread[] threads = new Thread[sessions.length];
		
		// Барьер для сессий + главного потока, чтобы главный ждал, пока все сессии отработают
		final CyclicBarrier cb = new CyclicBarrier(sessions.length + 1);

		// Инициализируем потоки
		for (int i = 0; i < sessions.length; i++) {
			final Session session = sessions[i];
			threads[i] = new Thread(new Runnable() {
				public void run() {
					session.doStart();
					System.out.println("\nРаботаем с FTP "
							+ session.getServer());
					for (File zippedFile : lookupFolder
							.listFiles(zipfiles)) {
						System.out.println("\nЗаливаем файл "
								+ zippedFile.getName() + " на FTP...");
						System.out.println("Полный путь к файлу "
								+ zippedFile.getAbsolutePath());

						// грузим нерадивый файл несколько раз
						boolean success = false;
						do{
							success = session.upload(zippedFile, ftpFolder_);
//							if(!success){
//								System.out.println("Нажмите любую клавишу для повторной попытки...");
//								try {
//									System.in.read();
//								} catch (IOException e) {
//								}
//							}
						}while(!success);
					}

					session.doEnd();
					Controller.getInstance().fireEvent(new Event(Events.UPLOAD_COMPLETED, null));
					try {
						cb.await();
					} catch (InterruptedException e) {
						// e.printStackTrace();
					} catch (BrokenBarrierException e) {
						// e.printStackTrace();
					}
				}
			});
		}

		// Запускаем потоки
		for (Thread thread : threads) {
			System.out.println("для потока id=" + thread.getId());
			// создаём окно, ассоциированное с потоком
			Controller.getInstance().fireEvent(
					new Event(Events.NEW_PROGRESS_WINDOW, thread.getId()));
			// запускаем поток
			thread.start();
		}
		// System.in.read();
		try {
			cb.await();
		} catch (InterruptedException e) {
			// e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// e.printStackTrace();
		}

		Controller.getInstance().fireEvent(new Event(Events.EXIT, null));
	}
}

class ZipFilenameFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		if (name.lastIndexOf('.') > 0) {
			// get last index for '.' char
			int lastIndex = name.lastIndexOf('.');

			// get extension
			String str = name.substring(lastIndex);

			// match path name extension
			if (str.equals(".zip")) {
				return true;
			}
		}
		return false;
	}
}