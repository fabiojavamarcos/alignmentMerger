package util;

public class EOFRDFExcetion extends Exception {

		private String errorMsg;

		public EOFRDFExcetion(String errorMsg) {
			super();
			this.errorMsg = errorMsg;
		}

		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
}
