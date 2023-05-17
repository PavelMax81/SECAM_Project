/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cellcore;

/** Class for simulation of bistable cellular automaton (BCA) where defect generation in material is realized
 * @author Pavel Maksimov and Dmitry Moiseenko
 */
public class BistableCA
{
    /** The number of bistable cells in BCA along axis X */
    int cell_number_X;
    
    /** The number of bistable cells in BCA along axis Y */
    int cell_number_Y;
    
    /** The number of bistable cells in BCA along axis Z */
    int cell_number_Z;
    
    /** Array of single indices of bistable cells */
    private int[] cell_indices;
    
    /** The constructor of the class
     */
    public BistableCA()
    {
        readParameters();
        
        cell_indices = new int[cell_number_X*cell_number_Y*cell_number_Z];
    }
    
    /** The method reads values of BCA parameters from the file.
     * @param  
     */
    private void readParameters()
    {
        
    }
    
    /** The method returns array of single indices of bistable cells.
     * @return array of single indices of bistable cells
     */
    public int[] getCellIndices()
    {
        return cell_indices;
    }
    
    /** The method returns the number of bistable cells in BCA along axis X.
     * @return the number of bistable cells in BCA along axis X
     */
    public int getCellNumberX()
    {
        return cell_number_X;
    }
    
    /** The method returns the number of bistable cells in BCA along axis Y.
     * @return the number of bistable cells in BCA along axis Y
     */
    public int getCellNumberY()
    {
        return cell_number_Y;
    }
    
    /** The method returns the number of bistable cells in BCA along axis Z.
     * @return the number of bistable cells in BCA along axis Z
     */
    public int getCellNumberZ()
    {
        return cell_number_Z;
    }
    
    /** The method sets the number of bistable cells in BCA along axis X.
     * @param _cell_number_X the number of bistable cells in BCA along axis X
     */
    public void setCellNumberX(int _cell_number_X)
    {
        cell_number_X = _cell_number_X;
    }
    
    /** The method sets the number of bistable cells in BCA along axis Y.
     * @param _cell_number_Y the number of bistable cells in BCA along axis Y
     */
    public void setCellNumberY(int _cell_number_Y)
    {
        cell_number_Y = _cell_number_Y;
    }
    
    /** The method sets the number of bistable cells in BCA along axis Z.
     * @param _cell_number_Z the number of bistable cells in BCA along axis Z
     */
    public void setCellNumberZ(int _cell_number_Z)
    {
        cell_number_Z = _cell_number_Z;
    }
}
