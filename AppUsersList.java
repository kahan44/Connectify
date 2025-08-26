class AppUsers {
    private String phoneNumber;
    private String name;
    private String password;
    private boolean loginStatus;

    public AppUsers(String phoneNumber, String name, String password) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getPassword() {
        return password;
    }
}



class List {
    class Node {
        AppUsers user;
        Node next;

        Node(AppUsers user) {
            this.user = user;
            this.next = null;
        }
    }
    Node head = null;

    public void add(AppUsers user) {
        Node newNode = new Node(user);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public boolean exists(String phoneNumber) {
        if(head == null) {
            return false;
        }
        Node current = head;
        while (current != null) {
            if (current.user.getPhoneNumber().equals(phoneNumber)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public String getPassword (String phoneNumber) {
        if(head == null) {
            return "";
        }
        Node current = head;
        while (current != null) {
            if (current.user.getPhoneNumber().equals(phoneNumber)) {
                return current.user.getPassword();
            }
            current = current.next;
        }
        return "";
    }

    public String getName (String phoneNumber) {
        if(head == null) {
            return "";
        }
        Node current = head;
        while (current != null) {
            if (current.user.getPhoneNumber().equals(phoneNumber)) {
                return current.user.getName();
            }
            current = current.next;
        }
        return "";
    }

    public void updateLoginStatus (String phone) {
        if(head == null) {
            return;
        }
        Node current = head;
        while (current != null) {
            if (current.user.getPhoneNumber().equals(phone)) {
                current.user.setLoginStatus(true);
                break;
            }
            current = current.next;
        }
    }

    public boolean checkLoginStatus (String phoneNumber) {
        if(head == null) {
            return false;
        }
        Node current = head;
        while (current != null) {
            if (current.user.getPhoneNumber().equals(phoneNumber)) {
                return current.user.isLoginStatus();
            }
            current = current.next;
        }
        return false;
    }

    public void updateListPassword (String phoneNumber, String newPassword) {
        Node current = head;
        while (current != null) {
            if (current.user.getPhoneNumber().equals(phoneNumber)) {
                current.user.setPassword(newPassword);
                break;
            }
            current = current.next;
        }
    }
}