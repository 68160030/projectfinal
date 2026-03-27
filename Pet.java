public abstract class Pet {
    private String name;
    private int energy = 100;

    public Pet(String name) { this.name = name; }
    public String getName() { return name; }
    public int getEnergy() { return energy; }
    
    public void decreaseEnergy() {
        if(energy > 0) energy -= 5;
    }

    public void feed() {
        energy += 30;
        if(energy > 100) energy = 100;
    }

    public abstract void makeSound();
    public abstract void play();
    public abstract String getImagePath(); 
}
