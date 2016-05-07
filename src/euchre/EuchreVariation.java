package euchre;

public class EuchreVariation {
	
	public EuchreVariation(String name) {
		this.name = name;
		if(this.name == null || this.name.trim().equals("")) {
			this.name = "ontarian";
		}
		this.name = this.name.toLowerCase();
		
		if(name.equals("bicycle")) {
			dealerPartnerOrdersUpAloneOrPass = false;
		} else {
			dealerPartnerOrdersUpAloneOrPass = true;
		}
		
	}
	
	
	private String name = "";
	
	public String getVariation() {
		return "Variation: " + name;
	}
	
	public boolean isDealerPartnerOrdersUpAloneOrPass() {
		return dealerPartnerOrdersUpAloneOrPass;
	}



	private boolean dealerPartnerOrdersUpAloneOrPass = true;

}
