package fr.recia.menucantine.adoria.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/*
 Entrée>15% lipides	Entrées contenant plus de 15 % de lipides	2	#FECA0A
Crudités légume ou fruit	Crudités de légumes ou de fruits, contenant au moins 50% de légumes ou de fruits	3	#BAD65D
Produit à frire/Pré-frits	Produits à frire ou pré-frits contenant plus de 15 % de lipides	4	#FECA0A
Plat Protidique P/L>1	Plats protidiques ayant un rapport P/L = 1	5	#ED1D24
Poisson/Préparat° Poisson	Poissons ou préparations à base de poisson contenant au moins 70 % de poisson, et ayant un P/L = 2	6	#ED1D24
Viande non hachée/abats	Viandes non hachées de bœuf, de veau ou d’agneau, et abats de boucherie	7	#ED1D24
Préparati° à base viande	Préparations ou plats prêts à consommer à base de viande, de poisson, d’œuf et/ou de fromage, contenant moins de 70 % du grammage recommandé pour la denrée protidique des plats composés	8	#ED1D24
Légumes cuits	Légumes cuits, autres que secs, seuls, ou en mélange contenant au moins 50 % de légumes	9	#02A54F
Légume sec féculent céréa	Légumes secs, féculents ou céréales, seuls, ou en mélange contenant au moins 50 % de légumes secs, féculents ou céréales	10	#8C442B
Fromage <150mg calc	Fromages contenant au moins 150 mg de calcium laitier par portion (4)	11	#02AFEF
Fromage 100 à 150mg calc	Fromages dont la teneur en calcium laitier est comprise entre 100mg et moins de 150mg par portion (4)	12	#02AFEF
Fromage>100mg calc <5%lip	Produits laitiers ou desserts lactés contenant plus de 100 mg de calcium laitier, et moins de 5g de lipides par portion	13	#02AFEF
Dessert>15% lipides	Desserts contenant plus de 15 % de lipides	14	#FECA0A
Dessert>20g gluc <15% lip	Desserts ou produits laitiers contenant plus de 20g de glucides simples totaux par portion et moins de 15% de lipides	15	#8C442B
Dessert 100% fruits crus	Desserts de fruits crus 100% fruit cru, sans sucre ajouté(5) 8/20 mini	16	#BAD65D
Plat préparé	Préparations ou plats prêts à consommer contenant moins de 70 % du grammage recommandé pour la portion de viande, poisson ou œuf	17	#ED1D24


 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GemRcn {
		Autre("","", 0, "#fff"),
		Entree15Lipide("Entrée>15% lipides", "Entrées contenant plus de 15 % de lipides",	2,	"#FECA0A"),
		Crudités("Crudités légume ou fruit", "Crudités de légumes ou de fruits, contenant au moins 50% de légumes ou de fruits",	3,	"#BAD65D"),
		Friture("Produit à frire/Pré-frits",	"Produits à frire ou pré-frits contenant plus de 15 % de lipides",	4,	"#FECA0A"),
		PlatProtidique("Plat Protidique P/L>1",	"Plats protidiques ayant un rapport P/L = 1",	5,	"#ED1D24"),
		Poisson("Poisson/Préparat° Poisson",	"Poissons ou préparations à base de poisson contenant au moins 70 % de poisson, et ayant un P/L = 2",	6,	"#ED1D24"),
		Viande("Viande non hachée/abats",	"Viandes non hachées de bœuf, de veau ou d’agneau, et abats de boucherie",	7, "#ED1D24"),
		ViandePreparation("Préparati° à base viande",	"Préparations ou plats prêts à consommer à base de viande, de poisson, d’œuf et/ou de fromage, contenant moins de 70 % du grammage recommandé pour la denrée protidique des plats composés",	8, "#ED1D24"),
		LegumesCuits("Légumes cuits", "Légumes cuits, autres que secs, seuls, ou en mélange contenant au moins 50 % de légumes",	9,	"#02A54F"),
		LegumeSec("Légume sec féculent céréa",	"Légumes secs, féculents ou céréales, seuls, ou en mélange contenant au moins 50 % de légumes secs, féculents ou céréales",	10,	"#8C442B"),
		Fromage("Fromage <150mg calc",	"Fromages contenant au moins 150 mg de calcium laitier par portion (4)",	11,	"#02AFEF"),
		FromageCalc("Fromage 100 à 150mg calc",	"Fromages dont la teneur en calcium laitier est comprise entre 100mg et moins de 150mg par portion (4)",12,	"#02AFEF"),
		FromageLip("Fromage>100mg calc <5%lip",	"Produits laitiers ou desserts lactés contenant plus de 100 mg de calcium laitier, et moins de 5g de lipides par portion",	13,	"#02AFEF"),
		DessertLip("Dessert>15% lipides", "Desserts contenant plus de 15 % de lipides",	14,	"#FECA0A"),
		DessertGluc("Dessert>20g gluc <15% lip", "Desserts ou produits laitiers contenant plus de 20g de glucides simples totaux par portion et moins de 15% de lipides",	15,	"#8C442B"),
		DessertFruit("Dessert 100% fruits crus",	"Desserts de fruits crus 100% fruit cru, sans sucre ajouté(5) 8/20 mini",	16,	"#BAD65D"),
		PlatPrepae("Plat préparé", "Préparations ou plats prêts à consommer contenant moins de 70 % du grammage recommandé pour la portion de viande, poisson ou œuf",	17,	"#ED1D24"),
		Fromage150Calc("Fromage> 150mg calc", "Fromages contenant au moins 150 mg de calcium laitier par portion", 18, "#02AFEF"),
		ProduitLaitier("P.Lait>100mg calc <5%lip",	"Produits laitiers ou desserts lactés contenant plus de 100 mg de calcium laitier, et moins de 5g de lipides par portion",	19,	"#02AFEF")
	;
		
	private static final Logger log = LoggerFactory.getLogger(GemRcn.class);	
	
	static private Map<String, GemRcn> s2GemRcn = null;

	
	String codeS;
	
	@Getter
	String comment;
	
	@Getter
	int codeI;
	
	@Getter
	String color;
	
	static List<GemRcn> listAll;
	
	private GemRcn(String codeS, String comment, int codeI, String color) {
		this.codeS = codeS;
		this.comment = comment;
		this.codeI = codeI;
		this.color = color;
	}
	
	static public GemRcn getGemRcn(String codeS) {
		Map<String, GemRcn> map = s2GemRcn;
		if (map == null) {
			map = new HashMap<>();
			for (GemRcn g  : GemRcn.values()) {
				map.put(g.codeS, g);
			}
			s2GemRcn = map;
		}
		GemRcn g = map.get(codeS);
		if (g == null) {
			log.warn("not found: {}", codeS);
		}
		return g == null ? GemRcn.Autre : g;
	}
	
	static public List<GemRcn> getList(){
		List<GemRcn> list = listAll;
		if (list == null) {
			list = new ArrayList<>();
			for (GemRcn gr : GemRcn.values()) {
				int idx = gr.codeI;
				for (int i = list.size(); i <= idx; i++) {
					list.add(null);
				}
				list.set(idx, gr);
			}
			listAll = list;
		}
		return list;
	}

}
