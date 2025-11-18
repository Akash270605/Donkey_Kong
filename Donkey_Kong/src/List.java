/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public interface List<T> {
    public boolean add(T x);
    
    public boolean add(int index, T x);
    
    public int size();
    
    public T get(int index);
    
    public T set(int index, T x);
    
    public T remove(int index);
    
    public boolean isEmpty();
    
    public boolean contains(T element);
    
    public int indexOf(T element);
}
