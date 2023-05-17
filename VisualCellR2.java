package util;

/*
 * @(#) VisualCellR2.java version 1.0.0;       October 28 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for visualization of cell in 2D-space
 *
 *=============================================================
 *  Last changes :
 *          28 October 2009 by Pavel V. Maksimov (creation of the class)
 *          File version 1.0.0
 */

/** Class for visualization of cell in 2D-space
 *  @author Dmitry D. Moiseenko, Pavel V. Maksimov, Gregory S. Bikineev
 *  @version 1.0.0 - October 2009
 */
public class VisualCellR2 extends VisualCellR1
{
    /** Coordinate Y of cell */
    protected double coord_Y;    
    
    /** The constructor creates representative of class for visualization of cell in 2D-space.
     * @param _material material of cell
     * @param _energy_type type of energy interaction with neighbours
     * @param _grain_index index of grain containing cell
     * @param _location_type type of cell location in grain and in specimen
     * @param _state state of cell
     * @param _coord_X coordinate X of cell
     * @param _coord_Y coordinate Y of cell
     * @param _temperature temperature of cell
     * @param _eff_stress "effective" stress of cell
     * @param _prin_stress principal stress of cell
     * @param _force_moment_X coordinate X of force moment of cell
     * @param _force_moment_Y coordinate Y of force moment of cell
     * @param _force_moment_Z coordinate Z of force moment of cell
     * @param _strain strain of cell
     * @param _discl_density disclination density of cell
     */
    public VisualCellR2(String _material, byte _energy_type, int _grain_index, byte _location_type, byte _state,
                        double _coord_X, double _coord_Y,
                        double _temperature, double _eff_stress, double _prin_stress,
                        double _force_moment_X, double _force_moment_Y, double _force_moment_Z, 
                        double _strain, double _discl_density)
    {        
        super(_material, _energy_type, _grain_index, _location_type, _state, _coord_X,
              _temperature, _eff_stress, _prin_stress,
              _force_moment_X, _force_moment_Y, _force_moment_Z, _strain, _discl_density);

        coord_Y = _coord_Y;
    }

    /** The constructor creates representative of class for visualization of cell in 2D-space.
     * @param _visualCellR2 representative of class for visualization of cell in 2D-space
     */
    public VisualCellR2(VisualCellR2 _visualCellR2)
    {
        super(_visualCellR2);

        coord_Y = _visualCellR2.getCoordY();
    }

    /** The constructor creates default representative of class for visualization of cell in 2D-space.
     */
    public VisualCellR2()
    {
        super();

        coord_Y = 0;
    }
    
    /** The method returns coordinate Y of cell.
     * @return coordinate Y of cell
     */
    public double getCoordY()
    {
        return coord_Y;
    }
    
    /** The method sets coordinate Y of cell
     * @param _coord_Y coordinate Y of cell
     */
    public void setCoordY(double _coord_Y)
    {
        coord_Y = _coord_Y;
    }
}
