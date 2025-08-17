package Stock_Predictor.PortFolioManagment;


class Node {
    Portfolio data;
    Node next;
    Node(Portfolio x)
    {
        data=x;
    }
}
public class LinkedList {
    Node head;
    public void addfirst(Portfolio x) {
        Node n = new Node(x);
        if (head == null) {
            head = n;
        } else {
            n.next = head;
            head = n;
        }
    }

    public void addLast(Portfolio x) {
        Node n = new Node(x);
        if (head == null) {
            head = n;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = n;
        }
    }

    public void deletefirst() {
        if (head == null) {
            System.out.println("underflow");
        } else {
            head = head.next;
        }
    }

    public void deletelast() {
        if (head == null) {
            System.out.println("underflow");
        } else if (head.next == null) {
            head = null;
        } else {
            Node temp = head;
            while (temp.next.next != null) {
                temp = temp.next;
            }
            temp.next = null;

        }
    }

    public void display() {
        if (head == null) {
            System.out.println("List is empty.");
        } else {
            Node temp = head;
            System.out.print("Linked List: ");
            while (temp != null) {
                System.out.print(temp.data + "-->");
                temp = temp.next;
            }
            System.out.println("null");
        }
    }
    public void insertBeforeValue(Portfolio x, Portfolio value) {
        int flag = 0;

        if (head == null) {
            System.out.println("Linked list is empty");
        } else {
            Node temp1 = head;
            while (temp1 != null) {
                if (temp1.data == x) {
                    flag = 1;
                }
                temp1 = temp1.next;
            }

            if (flag == 0) {
                System.out.println("The asked value is not inside the linked list");
            } else {
                Node n = new Node(value);

                if (head.data == x)
                {
                    addfirst(value);
                }
                else if (head.data==x)
                {
                    addfirst(value);
                }
                else {
                    Node temp2 = head;
                    while (temp2.next.data != x) {
                        temp2 = temp2.next;
                    }
                    n.next = temp2.next;
                    temp2.next = n;
                }
            }
        }
    }
    public void insertAfterValue(Portfolio x, Portfolio value) //created a method toadd the value after a particular value in the linked list//
    {
        int flag = 0;
        if (head == null)
        {
            System.out.println("Linked is empty");
        }
        else
        {
            Node temp1 = head;
            while (temp1 != null)
            {
                if (temp1.data == x)
                {
                    flag = 1;
                }
                temp1 = temp1.next;
            }

            if (flag == 0)
            {
                System.out.println("The asked value is not inside the linked list");
            }
            else
            {
                Node n = new Node(value);
                if (head.data == value && head.next == null)
                {
                    head.next = n;
                }
                else if (head.data == x)
                {
                    n.next = head.next;
                    head.next = n;
                }
                else
                {
                    Node temp2 = head;
                    while (temp2.data != x)
                    {
                        temp2 = temp2.next;
                    }
                    n.next = temp2.next;
                    temp2.next = n;
                }
            }
        }
    }
    public void deleteValue(Portfolio value)
    {
        int flag = 0;
        if (head == null)
        {
            System.out.println("Linked is empty");
        }
        else
        {
            Node temp1 = head;

            while (temp1 != null)
            {
                if (temp1.data == value)
                {
                    flag = 1;
                }
                temp1 = temp1.next;
            }

            if (flag == 0)
            {
                System.out.println("The asked value is not inside the linked list");
            }
            else
            {
                if (head.data == value && head.next == null)
                {
                    head = null;
                }
                else if (head.data == value)
                {
                    head = head.next;
                }
                else {
                    Node temp2 = head;
                    while (temp2.next.data != value) {
                        temp2 = temp2.next;
                    }
                    Node q = temp2.next;
                    temp2.next = q.next;
                    q = null;
                }
            }
        }
    }
    public boolean isEmpty(){
        if(head==null){
            return true;
        }
        else {
            return false;
        }
    }
}

