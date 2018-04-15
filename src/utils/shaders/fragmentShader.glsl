#version 330

#define AMBIENT_C 0
#define DIFFUSE_C 1
#define SPECULAR_C 2
#define EMISSIVE_C 3

struct GOut{
	vec2 texcoord;
	vec3 world_position;
	vec3 world_normal;
};

struct Textures{
	sampler2D ambient;
	sampler2D diffuse;
	sampler2D specular;
	sampler2D hight_map;
};

struct Light
{
	vec3 position;
	vec3 direction;
	vec3 color;
};

struct MaterialStandard
{
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
    vec4 emissive;
};

uniform Textures u_textures;
uniform mat4 u_material_table;

in Light light;
in vec3 look_direction;

in GOut v_out;

out vec4 frag_color;

MaterialStandard material;

float lerp(float a, float b, float f){
    return (1.0 - f) * a + b * f;
}

void lambertLighting(inout MaterialStandard mat, in Light light, in vec3 normal){
	vec3 encoded_normal = normal * 0.5 + 0.5;
	float ndotl = max(dot(encoded_normal, -light.direction), 0.0);
	mat.diffuse = mat.diffuse * mat.ambient * ndotl * vec4(light.color, 1.0);
}

void phongLighting(inout MaterialStandard mat, in Light light, in vec3 normal, in vec3 look_dir){
    vec3 reflect = normalize(reflect(-light.direction, normal));
    mat.specular = vec4(mat.specular.rgb * pow(max(0.0, dot(reflect, -look_dir)), mat.specular.w), 1.0);
}

void main(){
    /*Material*/
    material.ambient = u_material_table[AMBIENT_C];
    material.diffuse = u_material_table[DIFFUSE_C];
    material.specular = u_material_table[SPECULAR_C];
    material.emissive = u_material_table[EMISSIVE_C];

    vec4 ambient = texture(u_textures.ambient, v_out.texcoord) * material.ambient;
    material.ambient = ambient;

    lambertLighting(material, light, v_out.world_normal);

    float shininess = texture(u_textures.specular, v_out.texcoord).r * material.specular.w;
    material.specular.w = shininess;
    
	phongLighting(material, light, v_out.world_normal, look_direction);
	frag_color = material.diffuse + material.specular;
}
