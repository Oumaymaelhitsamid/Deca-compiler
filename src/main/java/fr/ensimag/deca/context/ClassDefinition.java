package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.DAddr;
/**
 * Definition of a class.
 *
 * @author gl44
 * @date 01/01/2022
 */
public class ClassDefinition extends TypeDefinition {


    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public int incNumberOfFields() {
        this.numberOfFields++;
        return numberOfFields;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }

    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    public EnvironmentExp getEnvironment() {
        return members;
    }

    private final EnvironmentExp members;
    private final ClassDefinition superClass;

    public EnvironmentExp getMembers() {
        return members;
    }

    private DAddr operand;

    public void setOperand(DAddr operand){
        this.operand = operand;
    }

    public DAddr getOperand(){
        return this.operand;
    }

    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }

}
