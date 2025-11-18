/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class LinkedList<T> implements List<T> {
    private class Node{
        private T data;
        private Node next;
        
        public Node(T data){
            this.data = data;
        }
        
        public Node(T data, Node next){
            this.next = next;
            this.data = data;
        }
    }
    
    private Node head;
    private Node tail;
    
    public boolean add(T x){
        if(isEmpty()){
            head = new Node(x);
            tail = head;
            return true;
        }else{
            Node  current = head;
            
            while(current.next != null){
                current = current.next;
            }
            
            current.next = new Node(x);
            tail = current.next;
            return true;
        }
    }
    
    public boolean add(int index, T x){
        if(index >= size() || index < 0){
            throw new IllegalArgumentException("adding at invalid index");
        }
        
        if(index == 0){
            head = new Node(x, head);
            return true;
        }
        Node current = head;
        
        for(int i=0; i<index-1; i++){
            current = current.next;
        }
        current.next = new Node(x, current.next);
        tail = current.next;
        return true;
    }
    
    public int size(){
        if(isEmpty()){
            return 0;
        }
        int counter = 1;
        Node current = head;
        
        while(current.next != null){
            current = current.next;
            counter ++;
        }
        return counter;
    }
    
    public T get (int index){
        if(index >= size() || index < 0){
            throw new IllegalArgumentException("Invalid index");
        }
        int counter = 0;
        Node current  = head;
        
        while(counter < index){
            current = current.next;
            counter ++;
        }
        return current.data;
    }
    
    public T set(int index, T x){
        if(index >= size()){
            throw new IllegalArgumentException("Invalid index");
        }
        Node current = head;
        
        for(int i=0; i<index; i++){
            current = current.next;
        }
        T temp = current.data;
        current.data = x;
        return temp;
    }
    
    public T remove(int index){
        if(index >= size() || index < 0){
            throw new IllegalArgumentException("Invalid index");
        }
        Node previous = null;
        Node current = head;
        if(index == 0){
            T temp = head.data;
            head = head.next;
            return temp;
        }
        
        for(int i=0; i<index; i++){
            previous = current;
            current = current.next;
        }
        previous.next = current.next;
        return current.data;
    }
    
    public boolean isEmpty(){
        if(head == null) return true;
        return false;
    }
    
    public boolean contains(T element){
        if(isEmpty()){
            return false;
        }
        
        for(Node i = head; i != null; i=i.next){
            if(i.data.equals(element)) return true;
        }
        return false;
    }
    
    public int indexOf(T element){
        int idx = 0;
        
        for(Node i = head; i != null; i = i.next){
            if(i.data.equals(element)) return idx;
            idx++;
        }
        return -1;
    }
}
