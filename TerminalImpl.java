import terminalExceptions.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

class TerminalImpl implements Terminal {
    private TerminalServer accountsServer = new TerminalServer();
    private Calendar blockTime;

    TerminalImpl() {
    }

    void serviceStart() {
        Scanner userMenu = new Scanner(System.in);
        boolean validPin = false;
        Character userChoose = '?';
        do {
            if (userChoose != 63) {
                Long e;
                switch (userChoose) {
                    case '1':
                        this.accessInterrupt();
                        validPin = this.pinCheck();
                        break;
                    case '2':
                        this.accessInterrupt();
                        if (!validPin) {
                            throw new TerminalException("Select first pin authorisation",
                                    new IOException(), "Missing pin authorisation");
                        }
                        this.showPinInsert();
                        this.showUserAccount(this.accountCheck(userMenu.next()));
                        this.showConsoleMenu("valid pin");
                        break;
                    case '3':
                        this.accessInterrupt();
                        if (!validPin) {
                            throw new TerminalException("Select first pin authorisation",
                                    new IOException(), "Missing pin authorisation");
                        }
                        this.showAmountInsert();
                        e = userMenu.nextLong();
                        this.showPinInsert();
                        this.showUserAccount(this.getAmount(userMenu.next(), e));
                        this.showConsoleMenu("valid pin");
                        break;
                    case '4':
                        this.accessInterrupt();
                        if (!validPin) {
                            throw new TerminalException("Select first pin authorisation",
                                    new IOException(), "Missing pin authorisation");
                        }
                        this.showAmountInsert();
                        e = userMenu.nextLong();
                        this.showPinInsert();
                        this.showUserAccount(this.putAmount(userMenu.next(), e));
                        this.showConsoleMenu("valid pin");
                        break;
                    default:
                        this.accessInterrupt();
                        if (validPin) {
                            this.showConsoleMenu("valid pin");
                        } else {
                            this.showConsoleMenu("first show");
                        }
                }
            } else {
                this.showConsoleMenu("first show");
            }

            userChoose = userMenu.next().charAt(0);
        } while (userChoose != 48);

        try (TerminalServer e1 = this.accountsServer) {
            e1.close();
        } catch (TerminalServerException var27) {
            System.out.println(var27.getReason());
        } finally {
            this.showExitMessage();
        }

    }

    private void showConsoleMenu(String menuStatus) {
        byte var3 = -1;
        switch (menuStatus.hashCode()) {
            case -1111724175:
                if (menuStatus.equals("valid pin")) {
                    var3 = 1;
                }
                break;
            case -219011475:
                if (menuStatus.equals("first show")) {
                    var3 = 0;
                }
        }

        switch (var3) {
            case 0:
                System.out.println("--------------------------------\n" +
                        ">>>     Terminal console:    <<<\n" +
                        "--------------------------------\n" +
                        ">>>    Need authorisation:   <<<\n" +
                        "> 1 - enter your pin code      <\n" +
                        "> 2 - show your balance        <\n" +
                        "> 3 - take amount of money     <\n" +
                        "> 4 - insert amount of money   <\n" +
                        "> 0 - stop servicing           <\n" +
                        "--------------------------------");
                System.out.print(">>> ");
                break;
            case 1:
                System.out.println("--------------------------------\n" +
                        ">>>     Choose operation:    <<<\n" +
                        "> 2 - show your balance        <\n" +
                        "> 3 - take amount of money     <\n" +
                        "> 4 - insert amount of money   <\n" +
                        "> 0 - stop servicing           <\n" +
                        "--------------------------------");
                System.out.print(">>> ");
        }

    }

    private void showExitMessage() {
        System.out.println("--------------------------------\n" +
                ">>>       Exit service:      <<<\n" +
                "> Thank you for using terminal <\n" +
                "--------------------------------");
    }

    private void showPinInsert() {
        System.out.println("--------------------------------\n" +
                ">>>      Input your pin:     <<<\n" +
                "--------------------------------");
        System.out.print(">>> ");
    }

    private void showAmountInsert() {
        System.out.println("--------------------------------\n" +
                ">>> Input amount to get\\add: <<<\n" +
                "--------------------------------");
        System.out.print(">>> ");
    }

    private void showUserAccount(String balance) {
        System.out.println("--------------------------------\n" +
                ">>> Your balance: " + balance + "\n" +
                "--------------------------------");
    }

    private TerminalServer getAccountsServer() {
        return this.accountsServer;
    }

    public void setAccountsServer(TerminalServer accountsServer) {
        this.accountsServer = accountsServer;
    }

    private boolean pinCheck() {
        Scanner userMenu = new Scanner(System.in);
        boolean status = false;
        Calendar blockTime;
        int attempt = 3;
        do {
            this.showPinInsert();
            if (this.getAccountsServer().checkPin(userMenu.next())) {
                this.showConsoleMenu("valid pin");
                status = true;
                break;
            }
            --attempt;
            System.out.println("--------------------------------\n" +
                    ">>>       Incorrect pin:     <<<\n" +
                    ">>     You left " + attempt + " attempts    <<");
        } while (attempt != 0);
        if (attempt == 0) {
            this.blockUser();
        }

        return status;
    }

    public String accountCheck(String personalPin) {
        String resultAccount;
        try {
            resultAccount = this.getAccountsServer().getUserAccount(personalPin).toString();
        } catch (CheckAccountException var4) {
            resultAccount = var4.getReason();
        }

        return resultAccount;
    }

    public String getAmount(String personalPinHash, long amount) {
        String resultAccount;
        try {
            resultAccount = this.getAccountsServer().setAmount(personalPinHash, amount, "get").toString();
        } catch (AmountOperationException var6) {
            resultAccount = var6.getReason();
            System.out.println("--------------------------------\n" +
                    ">>>    Incorrect operation:   <<<\n" +
                    ">>  " + resultAccount);
        }

        return resultAccount;
    }

    public String putAmount(String personalPinHash, long amount) {
        String resultAccount;
        try {
            resultAccount = this.getAccountsServer().setAmount(personalPinHash, amount, "put").toString();
        } catch (AmountOperationException var6) {
            resultAccount = var6.getReason();
            System.out.println("--------------------------------\n" +
                    ">>>    Incorrect operation:   <<<\n" +
                    ">>  " + resultAccount);
        }

        return resultAccount;
    }

    private void accessInterrupt() {
        Calendar checkTime;
        if (this.blockTime != null) {
            checkTime = Calendar.getInstance();
            int unlockTime = 5 - (int) (checkTime.getTime().getTime() - this.blockTime.getTime().getTime()) / 1000;
            if (unlockTime < 5 && unlockTime > 0)
                throw new AccountIsLockedException("User account blocked, please wait " + unlockTime +
                        " seconds before continue", new IllegalAccessException(), "Account blocked", unlockTime);
        }
    }

    private void blockUser() {
        this.blockTime = Calendar.getInstance();
    }
}
