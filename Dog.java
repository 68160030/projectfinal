public class Dog extends Pet {
    public Dog(String name) { super(name); }

    @Override 
    public void makeSound() { System.out.println("Woof! Woof!"); }

    @Override 
    public void play() { 
        System.out.println(getName() + " runs and fetches the ball!");
    }
    
    @Override 
    public String getImagePath() { return "dog.png"; } 
}
