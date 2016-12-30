package common.utility;

import java.io.File;
import java.io.FilenameFilter;

import common.configurations.datamodels.Browser;

/**
 * @author sabarinath.s
 * Date: 29-Apr-2016	
 * Time: 12:23:04 pm 
 */

public class DownloadFilenameFilterFactory {


	public static DownloadFileNameFilter getFileNameFilter(Browser browser, String fileNameWithExtension){

		switch (browser){
			case FIREFOX:
				return fileNameWithExtension!=null?new FireFoxFilenameFilter(fileNameWithExtension):new FireFoxFilenameFilter();
			case CHROME:
				return fileNameWithExtension!=null?new ChromeFilenameFilter(fileNameWithExtension): new ChromeFilenameFilter();
			default:
				break;
		}
		return null;
	}

	public static abstract class DownloadFileNameFilter  implements FilenameFilter  {

		String fileName;

		public   DownloadFileNameFilter(String fileNameWithExtension){
			this.fileName = fileNameWithExtension;
		}
	}

	public static class FireFoxFilenameFilter extends DownloadFileNameFilter{


		public   FireFoxFilenameFilter(String fileNameWithExtension){
			super(fileNameWithExtension);
		}

		public   FireFoxFilenameFilter(){
			super(null);
		}

		@Override
		public boolean accept(File dir, String name) {
			if(fileName!= null)
				return name.contains(fileName+".part");
			else
				return name.contains(".part");
		}

	}


	public static class ChromeFilenameFilter  extends DownloadFileNameFilter{

		public   ChromeFilenameFilter(String fileNameWithExtension){
			super(fileNameWithExtension);
		}

		public   ChromeFilenameFilter(){
			super(null);
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().contains(".crdwonload");
		}

	}
}