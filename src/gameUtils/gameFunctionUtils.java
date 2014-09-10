package gameUtils;

import java.io.File;

public class gameFunctionUtils {

	//gets the number at the end of the file name & before .txt
	public static int getFileNum(File child) {
		child.getName().substring(0, child.getName().length() - 4);
		int num = 0;
		
		for(int i=child.getName().length() - 5; true; i--) {
			if(child.getName().charAt(i) >= '0' && child.getName().charAt(i) <= '9') {
				num = 10*num + (child.getName().charAt(i) - '0');
			} else {
				break;
			}
		}
		
		return num;
	}
}
