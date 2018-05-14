import java.time.LocalDate;

public class BusinessRecord {
	private int id;
	private String name;
	private String status;
	private LocalDate regDate;
	private LocalDate canDate;
	private LocalDate renewDate;
	private String stateNum;
	private String regState;
	private String abn;
	
	public BusinessRecord(int id, String name, String status, LocalDate regDate, 
			LocalDate canDate, LocalDate renewDate, String stateNum, String regState, String abn){
		this.id = id;
		this.name = name;
		this.status = status;
		this.regDate = regDate;
		this.canDate = canDate;
		this.renewDate = renewDate;
		this.stateNum = stateNum;
		this.regState = regState;
		this.abn = abn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getRegDate() {
		return regDate;
	}

	public void setRegDate(LocalDate regDate) {
		this.regDate = regDate;
	}

	public LocalDate getCanDate() {
		return canDate;
	}

	public void setCanDate(LocalDate canDate) {
		this.canDate = canDate;
	}

	public LocalDate getRenewDate() {
		return renewDate;
	}

	public void setRenewDate(LocalDate renewDate) {
		this.renewDate = renewDate;
	}

	public String getStateNum() {
		return stateNum;
	}

	public void setStateNum(String stateNum) {
		this.stateNum = stateNum;
	}

	public String getRegState() {
		return regState;
	}

	public void setRegState(String regState) {
		this.regState = regState;
	}

	public String getAbn() {
		return abn;
	}

	public void setAbn(String abn) {
		this.abn = abn;
	}
	
	public void printInfo() {
		String regDateSt, canDateSt, renewDateSt;
		regDateSt = (regDate == null) ? "" : regDate.toString();
		canDateSt = (canDate == null) ? "" : canDate.toString();
		renewDateSt = (renewDate == null) ? "" : renewDate.toString();
		
		System.out.println(id + "   " + name + "   " + status + "   " + regDateSt + "   " + canDateSt + "   " +
				renewDateSt + "   " + stateNum + "   " + regState + "   " + abn);
	}
	
}
