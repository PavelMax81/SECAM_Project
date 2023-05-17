package util;

/*
 * @(#) VisualCellR3.java version 1.0.0;       October 28 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for visualization of cell in 3D-space
 *
 *=============================================================
 *  Last changes :
 *          28 October 2009 by Pavel V. Maksimov (creation of the class)
 *          File version 1.0.0
 */

/** Class for visualization of cell in 3D-space
 *  @author Dmitry D. Moiseenko, Pavel V. Maksimov, Gregory S. Bikineev
 *  @version 1.0.0 - October 2009
 */
public class VisualCellR3 extends VisualCellR2
{
    /** Coordinate Z of cell */
    protected double coord_Z;

    /** The constructor creates representative of class for visualization of cell in 3D-space.
     * @param _material material of cell
     * @param _energy_type type of energy interaction with neighbours
     * @param _grain_index index of grain containing cell
     * @param _location_type type of cell location in grain and in specimen
     * @param _state state of cell
     * @param _coord_X coordinate X of cell
     * @param _coord_Y coordinate Y of cell
     * @param _coord_Z coordinate Z of cell
     * @param _temperature temperature of cell
     * @param _eff_stress "effective" stress of cell
     * @param _prin_stress principal stress of cell
     * @param _force_moment_X coordinate X of force moment of cell
     * @param _force_moment_Y coordinate Y of force moment of cell
     * @param _force_moment_Z coordinate Z of force moment of cell
     * @param _strain strain of cell
     * @param _discl_density disclination density of cell
     */
    public VisualCellR3(String _material, byte _energy_type, int _grain_index, byte _location_type, byte _state,
                        double _coord_X, double _coord_Y, double _coord_Z,
                        double _temperature, double _eff_stress, double _prin_stress,
                        double _force_moment_X, double _force_moment_Y, double _force_moment_Z, 
                        double _strain, double _discl_density)
    {
        super(_material, _energy_type, _grain_index, _location_type, _state, _coord_X, _coord_Y,
              _temperature, _eff_stress, _prin_stress,
              _force_moment_X, _force_moment_Y, _force_moment_Z, _strain, _discl_density);

        coord_Z = _coord_Z;
    }

    /** The constructor creates representative of class for visualization of cell in 3D-space.
     * @param _visualCellR3 representative of class for visualization of cell in 3D-space
     */
    public VisualCellR3(VisualCellR3 _visualCellR3)
    {
        super(_visualCellR3);

        coord_Z = _visualCellR3.getCoordZ();
    }

    /** The constructor creates default representative of class for visualization of cell in 3D-space.
     */
    public VisualCellR3()
    {
        super();

        coord_Z = 0;
    }

    /** The method returns coordinate Z of cell.
     * @return coordinate Z of cell
     */
    public double getCoordZ()
    {
        return coord_Z;
    }

    /** The method sets coordinate Z of cell
     * @param _coord_Z coordinate Z of cell
     */
    public void setCoordZ(double _coord_Z)
    {
        coord_Z = _coord_Z;
    }
}