#version 330

struct Vertex{
    vec3 position;
    vec2 texcoord;
    vec3 normal;
};

struct Matrices{
    mat4 mv_matrix;
    mat4 p_matrix;
};

struct GOut{
	vec2 texcoord;
	vec3 world_position;
    vec3 world_normal;
};

struct Light
{
	vec3 position;
	vec3 direction;
	vec3 color;
};

layout(location = 0) in Vertex a_vertex;
uniform Matrices u_matrices;

const vec3 light_position = vec3(0.0, 4.0, 0.0);
const vec3 light_color = vec3(1.0, 1.0, 1.0);

out Light light;
out vec3 look_direction;
out GOut v_out;

void main(){
    vec4 world_position = u_matrices.mv_matrix * vec4(a_vertex.position, 1.0);
    
    look_direction = normalize(world_position - inverse(u_matrices.mv_matrix)[3]).xyz;
    light.position = light_position;
    light.direction = normalize(world_position.xyz - light.position);
    light.color = light_color;

    v_out.texcoord = a_vertex.texcoord;
    v_out.world_normal = normalize(u_matrices.mv_matrix * vec4(a_vertex.normal, 0.0)).xyz;
    v_out.world_position = world_position.xyz;
	
	gl_Position = u_matrices.p_matrix * world_position;
}