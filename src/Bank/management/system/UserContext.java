package Bank.management.system;

public class UserContext {
    private final long formno;
    private final long accountNo;
    private final String name;

    public UserContext(long formno, long accountNo, String name) {
        this.formno = formno;
        this.accountNo = accountNo;
        this.name = name;
    }
    public long getFormno() { return formno; }
    public long getAccountNo() { return accountNo; }
    public String getName() { return name; }
}
