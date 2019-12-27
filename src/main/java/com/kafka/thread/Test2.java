package com.kafka.thread;

public class Test2 {
	public static void main(String[] args) {
		Runnable r = new Runnable() {
			public void run() {
				System.out.println(Thread.currentThread().getName() + "执行完成");
			}
		};
		Thread t1 = new Thread(r, "t1");
		Thread t2 = new Thread(r, "t2");
		Thread t3 = new Thread(r, "t3");

		try {
			t3.start();
			t3.join();
			t2.start();
			t2.join();
			t1.start();
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
