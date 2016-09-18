/**
 * 
 */
package com.farooq.jmx.application;

import java.util.ArrayList;
import java.util.List;

/**
 * @author faroooq
 *
 */
public class MyApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int i =1000;
		while(i<10){
			//System.out.println("My application is still running");
			List<String> s = new ArrayList();
			s.add(new String(i+"A"));
		
			
		}
		
		
		try{
			Thread.sleep(20000);
			
		}catch(InterruptedException e){
			e.printStackTrace();
			System.out.println("After waking up");
		}

		
	}

}
