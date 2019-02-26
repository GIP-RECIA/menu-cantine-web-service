
const modalTemplate = 
`<transition name="modal">
		<div class="modal-mask">
      		<div class="modal-wrapper">
        		<div class="modal-container">
					<span>{{ plat.name }}</span>
					<div class="ifPlat" v-if="plat.gemrcn"> 
						<div class="forColor" v-for="code_gemrcn in plat.gemrcn">
								<!-- span :style="'background-color:' + gemrcn_list[code_gemrcn].color + '; display:inline-block; width: 1ex; height: 1ex;'" >&nbsp;</span -->
								<span :style="'color:' + gemrcn_list[code_gemrcn].color + ';'">	{{ gemrcn_list[code_gemrcn].comment }} </span>
						</div>
					</div>
					<ul>
						<li v-for="elem in plat.nutritions">
							<span>{{ elem.name }}: </span><span>{{ elem.value }} </span><span>{{ elem.unit }}</span>
						</li>
					</ul>
              		<button class="modal-default-button" @click="$emit('close')">X</button>
				</div>
			</div>
		</div>
</transition>`



var data = new FormData();
//data.append( "json", JSON.stringify({content: 'testPost'}));

var initPost = function(etab, noSem){  
		return {headers: {
			'Accept': 'application/json',
      		'Content-Type': 'application/json',
      		'X-COM-PERSIST': 'TRUE',
    	},
    	method: 'post', 
    	credentials: "same-origin",
 //   	mode: "cors",
    	body: JSON.stringify({"semaine": noSem, "annee": 2019, "uai": etab }),
    }};
    
var initGet = { method: 'GET', };

var  laModal = {
	 data: function (){ 
			return { nutData:[{'name': 'test', 'value': 'val', 'unit':'u'}], };
	 	},
	  template: modalTemplate,
	 
	 props: [ 'plat', 'gemrcn_list'],
	};



const menuCantine = new Vue({
	el: '#menucantine',
	data: {	
		defaultStyle:{ display: 'none' },
		menuSemaine: '',
		debutPeriode: '',
		finPeriode: '',
		jours: '',
		plat: '',
		selected: '',
		noSemaine: '6',
		erreur: '',
		showModal: false,
		gemRcnData:'',
	},
	
	components: {
		'modal' : laModal,
	},
	
	created () {},
	methods: {
		displayModal: function (plat) {
			//$modal.setNut(nut);
			this.plat = plat;
			console.log(this.plat);
			this.showModal = true;
		},
		loadMenu: function () {
			this.menuSemaine = '';
			this.erreur = '';
			
			fetch('api/hello', initPost(this.selected, this.noSemaine))
			.then(response => response.json())
			.then(json => {
					if (json.ErrorCode) {
						this.erreur = json;
						this.menuSemaine = '';
						this.defaultStyle = { display: 'none' };
					} else {
						this.menuSemaine = json;
						this.debutPeriode = json.debut;
						this.finPeriode = json.fin;
						this.gemRcnData = json.allGemRcn;
						this.jours = json.jours;
						console.log(this.gemRcnData);
						this.defaultStyle = {};
					}
				})
			.catch(errot => console.error(error))
			;
		},
	}

})