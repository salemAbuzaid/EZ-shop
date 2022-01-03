package it.polito.ezshop.data;

public class ProductRFID {
        private String RFID;
        private String barCode;
        private boolean sold;

    public ProductRFID() { }

    public ProductRFID(String RFID, String barCode) {
            this.RFID = RFID;
            this.barCode = barCode;
            this.sold = false;
        }

        public String getRFID() {
            return RFID;
        }
        public void setRFID(String RFID) {
            this.RFID = RFID;
        }
        public String getBarCode() {
            return barCode;
        }
        public void setBarCode(String barCode) {
            this.barCode = barCode;
        }
        public boolean isSold(){
            return sold;
        }
        public void setSold(boolean sold){
            this.sold = sold;
        }
}
