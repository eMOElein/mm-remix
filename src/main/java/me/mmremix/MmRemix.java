package me.mmremix;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

public class MmRemix {

	private static int WAIT = 100;
	private static int DELAY = 1000;

	private static void exit(int exitCode) {
		System.exit(exitCode);
	}

	private static void installTray() throws AWTException, IOException {
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener((e) -> MmRemix.exit(0));

		PopupMenu popup = new PopupMenu();
		popup.add(exitItem);

		TrayIcon trayIcon = new TrayIcon(ImageIO.read(MmRemix.class.getResource("/icon_16x16.png")), "mmRemix");
		trayIcon.setPopupMenu(popup);

		SystemTray.getSystemTray().add(trayIcon);
	}

	private static void loadProperties() throws FileNotFoundException, IOException {
		File file = new File("mm-remix.properties");
		if (!file.exists()) {
			return;
		}

		FileInputStream in = new FileInputStream(file);
		Properties p = new Properties();

		try {
			p.load(new FileInputStream(file));
		} finally {
			in.close();
		}

		if (p.containsKey("wait")) {
			Object value = p.getProperty("wait");
			WAIT = Integer.parseInt(value.toString());
		}

		if (p.containsKey("delay")) {
			Object value = p.getProperty("delay");
			DELAY = Integer.parseInt(value.toString());
		}

	}

	public static void main(String[] args) throws Exception {
		loadProperties();
		installTray();

		Thread t = new Thread(MmRemix::mouseMoverChecked);
		t.start();
	}

	private static void mouseMover() throws InterruptedException, AWTException {
		double totalWait = 0;
		int previousX = MouseInfo.getPointerInfo().getLocation().x;
		int previousY = MouseInfo.getPointerInfo().getLocation().y;
		Random random = new Random();
		Robot robot = new Robot();

		while (true) {
			Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
			int mouseX = mouseLocation.x;
			int mouseY = mouseLocation.y;

			if (mouseX == previousX && mouseY == previousY) {
				totalWait += WAIT;
			} else {
				totalWait = 0;
			}

			if (totalWait > DELAY) {
				mouseX += random.nextInt(-5, 6);
				mouseY += random.nextInt(-5, 6);
				robot.mouseMove(mouseX, mouseY);
			}

			previousX = mouseX;
			previousY = mouseY;

			Thread.sleep(WAIT);
		}

	}

	private static void mouseMoverChecked() {
		try {
			mouseMover();
		} catch (Exception e) {
			e.printStackTrace();
			exit(1);
		}
	}

}
