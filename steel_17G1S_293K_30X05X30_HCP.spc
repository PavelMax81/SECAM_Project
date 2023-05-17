aluminium

# Name of file with parameters of grain structure
grains_file      = ./user/spec_db/steel_17G1S_293K_30X05X30_HCP/tension_X.grn

# Variable responsible for type of packing of cellular automata:
# 0 - hexagonal close packing (HCP),
# 1 - simple cubic packing (SCP).
packing_type = 0

# Size of specimen in direction of X-axis (in meters)
specimen_size_X  =  3.6E-5

# Size of specimen in direction of Y-axis (in meters)
specimen_size_Y  =  7.000000000000001E-6

# Size of specimen in direction of Z-axis (in meters)
specimen_size_Z  =  3.9E-5

# Number of finite elements in direction of X-axis
element_number_X =  1

# Number of finite elements in direction of Y-axis
element_number_Y =  1

# Number of finite elements in direction of Z-axis
element_number_Z =  1

# Number of cells in finite element in direction of X-axis
cell_number_X    = 36

# Number of cells in finite element in direction of Y-axis
cell_number_Y    = 7

# Number of cells in finite element in direction of Z-axis
cell_number_Z    = 39

# Volume fraction of particles
particles_volume_fraction =   0.0

# Radius of particle
particle_radius =   0.0

# Minimal number of neighbour cells in adjacent grain necessary
# for transition of 'central' cell to adjacent grain
min_neighbours_number     = 4

# Type of interaction of boundary cells with neighbours:
# 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
# 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
# 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
bound_interaction_type = 2

# Type of functional dependence of boundary cell parameter on its coordinates
bound_function_type = 0

# Type of functional dependence of boundary cell parameter on time
bound_time_function_type = 0

# Portion of total time period when boundary cells are loaded
bound_load_time_portion = 0.0

# Variable responsible for account of anisotropy of simulated medium
anisotropy        = 0

# Coefficient of anisotropy of simulated medium
spec_anis_coeff   = 1.0

# Vector of anisotropy of simulated medium
anis_vector_X     = 1.0
anis_vector_Y     = 0.0
anis_vector_Z     = 0.0

# Coefficient of outer anisotropy
outer_anis_coeff   = 1.0

# Vector of outer anisotropy
outer_anis_vector_X     = 1.0
outer_anis_vector_Y     = 0.0
outer_anis_vector_Z     = 0.0

