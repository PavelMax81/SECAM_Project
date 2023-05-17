package util;

/*
 * @(#) VisualCellR1.java version 1.0.0;       October 28 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for visualization of cell in 1D-space
 *
 *=============================================================
 *  Last changes :
 *          28 October 2009 by Pavel V. Maksimov (creation of the class)
 *          File version 1.0.0
 */

/** Class for visualization of cell in 1D-space
 *  @author Dmitry D. Moiseenko, Pavel V. Maksimov, Gregory S. Bikineev
 *  @version 1.0.0 - October 2009
 */
public class VisualCellR1
{
    /** Material of cell */
    protected String material;

    /** Type of cell according to its energy interaction with neighbours */
    protected byte energy_type;

    /** Type of cell location in grain and specimen */
    protected byte location_type;

    /** Index of grain containing cell */
    protected int grain_index;

    /** State of cell (elastic, plastic, damaged) */
    protected byte state;

    /** Coordinate X of cell */
    protected double coord_X;

    /** Temperature of cell */
    protected double temperature;

    /** "Effective" stress of cell */
    protected double eff_stress;

    /** Principal stress of cell */
    protected double prin_stress;

    /** Coordinate X of force moment of cell */
    protected double force_moment_X;

    /** Coordinate Y of force moment of cell */
    protected double force_moment_Y;

    /** Coordinate Z of force moment of cell */
    protected double force_moment_Z;

    /** Strain of cell */
    protected double strain;

    /** Disclination density of cell */
    protected double discl_density;

    /** The constructor creates representative of class for visualization of cell in 1D-space.
     * @param _material material of cell
     * @param _energy_type type of energy interaction with neighbours
     * @param _grain_index index of grain containing cell
     * @param _location_type type of cell location in grain and in specimen
     * @param _state state of cell
     * @param _coord_X coordinate X of cell
     * @param _temperature temperature of cell
     * @param _eff_stress "effective" stress of cell
     * @param _prin_stress principal stress of cell
     * @param _force_moment_X coordinate X of force moment of cell
     * @param _force_moment_Y coordinate Y of force moment of cell
     * @param _force_moment_Z coordinate Z of force moment of cell
     * @param _strain strain of cell
     * @param _discl_density disclination density of cell
     */
    public VisualCellR1(String _material, byte _energy_type, int _grain_index, byte _location_type, byte _state,
                        double _coord_X,  double _temperature, double _eff_stress, double _prin_stress,
                        double _force_moment_X, double _force_moment_Y, double _force_moment_Z, 
                        double _strain, double _discl_density)
    {
        setMaterial(_material);
        energy_type = _energy_type;
        grain_index    = _grain_index;
        location_type  = _location_type;
        state          = _state;
        coord_X        = _coord_X;
        temperature    = _temperature;
        eff_stress     = _eff_stress;
        prin_stress    = _prin_stress;
        force_moment_X = _force_moment_X;
        force_moment_Y = _force_moment_Y;
        force_moment_Z = _force_moment_Z;
        strain         = _strain;
        discl_density  = _discl_density;
    }

    /** The constructor creates representative of class for visualization of cell in 1D-space.
     * @param _visualCellR1 representative of class for visualization of cell in 1D-space
     */
    public VisualCellR1(VisualCellR1 _visualCellR1)
    {
        setMaterial(_visualCellR1.getMaterial());
        energy_type    = _visualCellR1.getEnergyType();
        grain_index    = _visualCellR1.getGrainIndex();
        location_type  = _visualCellR1.getLocationType();
        state          = _visualCellR1.getState();
        coord_X        = _visualCellR1.getCoordX();
        temperature    = _visualCellR1.getTemperature();
        eff_stress     = _visualCellR1.getEffStress();
        prin_stress    = _visualCellR1.getPrinStress();
        force_moment_X = _visualCellR1.getForceMomentX();
        force_moment_Y = _visualCellR1.getForceMomentY();
        force_moment_Z = _visualCellR1.getForceMomentZ();
        strain         = _visualCellR1.getStrain();
        discl_density  = _visualCellR1.getDisclDensity();
    }

    /** The constructor creates default representative of class for visualization of cell in 1D-space.
     */
    public VisualCellR1()
    {
        material       = Common.NOT_AVAILABLE;
        energy_type    = 0;
        grain_index    = 0;
        location_type  = 0;
        state          = Common.ELASTIC_CELL;
        coord_X        = 0;
        temperature    = 0;
        eff_stress     = 0;
        prin_stress    = 0;
        force_moment_X = 0;
        force_moment_Y = 0;
        force_moment_Z = 0;
        strain         = 0;
        discl_density  = 0;
    }

    /** The method returns material of cell.
     * @return material of cell
     */
    public String getMaterial()
    {
        return material;
    }

    /** The method returns type of cell according to its energy interaction with neighbours
     * @return type of cell according to its energy interaction with neighbours
     */
    public byte getEnergyType()
    {
        return energy_type;
    }

    /** The method returns index of grain containing cell.
     * @return index of grain containing cell
     */
    public int getGrainIndex()
    {
        return grain_index;
    }

    /** The method returns type of cell location in grain and specimen.
     * @return type of cell location in grain and specimen
     */
    public byte getLocationType()
    {
        return location_type;
    }

    /** The method returns state of cell (elastic, plastic, damaged).
     * @return state of cell (elastic, plastic, damaged)
     */
    public byte getState()
    {
        return state;
    }

    /** The method returns coordinate X of cell.
     * @return coordinate X of cell
     */
    public double getCoordX()
    {
        return coord_X;
    }

    /** The method returns temperature of cell.
     * @return temperature of cell
     */
    public double getTemperature()
    {
        return temperature;
    }

    /** The method returns "effective" stress of cell.
     * @return "effective" stress of cell
     */
    public double getEffStress()
    {
        return eff_stress;
    }

    /** The method returns principal stress of cell.
     * @return principal stress of cell
     */
    public double getPrinStress()
    {
        return prin_stress;
    }

    /** The method returns coordinate X of force moment of cell.
     * @return coordinate X of force moment of cell
     */
    public double getForceMomentX()
    {
        return force_moment_X;
    }

    /** The method returns coordinate Y of force moment of cell.
     * @return coordinate Y of force moment of cell
     */
    public double getForceMomentY()
    {
        return force_moment_Y;
    }

    /** The method returns coordinate X of force moment of cell.
     * @return coordinate Z of force moment of cell
     */
    public double getForceMomentZ()
    {
        return force_moment_Z;
    }

    /** The method returns strain of cell.
     * @return strain of cell
     */
    public double getStrain()
    {
        return strain;
    }

    /** The method returns disclination density of cell.
     * @return disclination density of cell
     */
    public double getDisclDensity()
    {
        return discl_density;
    }

    /** The method sets material of cell.
     * @param _material material of cell
     */
    public void setMaterial(String _material)
    {
        material = new String(_material);
    }

    /** The method sets type of cell according to its energy interaction with neighbours
     * @param _energy_type type of cell according to its energy interaction with neighbours
     */
    public void setEnergyType(byte _energy_type)
    {
        energy_type = _energy_type;
    }

    /** The method sets index of grain containing cell.
     * @param _grain_index index of grain containing cell
     */
    public void setGrainIndex(int _grain_index)
    {
        grain_index = _grain_index;
    }

    /** The method sets type of cell location in grain and specimen.
     * @param _location_type type of cell location in grain and specimen
     */
    public void setLocationType(byte _location_type)
    {
        location_type = _location_type;
    }

    /** The method sets state of cell (elastic, plastic, damaged).
     * @param _state state of cell (elastic, plastic, damaged)
     */
    public void setState(byte _state)
    {
        state = _state;
    }

    /** The method sets coordinate X of cell
     * @param _coord_X coordinate X of cell
     */
    public void setCoordX(double _coord_X)
    {
        coord_X = _coord_X;
    }

    /** The method sets temperature of cell.
     * @param _temperature temperature of cell
     */
    public void setTemperature(double _temperature)
    {
        temperature = _temperature;
    }

    /** The method sets "effective" stress of cell.
     * @param _eff_stress "effective" stress of cell
     */
    public void setEffStress(double _eff_stress)
    {
        eff_stress = _eff_stress;
    }

    /** The method sets principal stress of cell.
     * @param _prin_stress principal stress of cell
     */
    public void setPrinStress(double _prin_stress)
    {
        prin_stress = _prin_stress;
    }

    /** The method sets coordinate X of force moment of cell.
     * @param _force_moment_X coordinate X of force moment of cell
     */
    public void setForceMomentX(double _force_moment_X)
    {
        force_moment_X = _force_moment_X;
    }

    /** The method sets coordinate Y of force moment of cell.
     * @param _force_moment_Y coordinate Y of force moment of cell
     */
    public void setForceMomentY(double _force_moment_Y)
    {
        force_moment_Y = _force_moment_Y;
    }

    /** The method sets coordinate X of force moment of cell.
     * @param _force_moment_Z coordinate Z of force moment of cell
     */
    public void setForceMomentZ(double _force_moment_Z)
    {
        force_moment_Z = _force_moment_Z;
    }

    /** The method sets strain of cell.
     * @param _strain strain of cell
     */
    public void setStrain(double _strain)
    {
        strain = _strain;
    }

    /** The method sets disclination density of cell.
     * @param _discl_density disclination density of cell
     */
    public void getDisclDensity(double _discl_density)
    {
        discl_density = _discl_density;
    }
}
