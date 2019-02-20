
const modalTemplate = 
`<transition name="modal">
		<div class="modal-mask">
      		<div class="modal-wrapper">
        		<div class="modal-container">
					<span>{{ plat.name }}</span>
					<ul>
						<li v-for="elem in plat.nutritions">
							<span>{{ elem.name }}:</span><span>{{ elem.value }}:</span><span>{{ elem.unit }}:</span>
						</li>
					</ul>
              		<button class="modal-default-button" @click="$emit('close')">X</button>
				</div>
			</div>
		</div>
</transition>`



var data = new FormData();
//data.append( "json", JSON.stringify({content: 'testPost'}));

var initPost = {  
		headers: {
			'Accept': 'application/json',
      		'Content-Type': 'application/json',
      		'X-COM-PERSIST': 'TRUE',
    	},
    	method: 'post', 
    	credentials: "same-origin",
 //   	mode: "cors",
    	body: '{"semaine": 6, "annee": 2019, "uai": "0180823X" }',
    };
var initGet = { method: 'GET', };

var  laModal = {
	 data: function (){ 
			return { nutData:[{'name': 'test', 'value': 'val', 'unit':'u'}], };
	 	},
	  template: modalTemplate,
	 
	 props: [ 'plat'],
	};



const menuCantine = new Vue({
	el: '#menucantine',
	data: {	
		menuSemaine: '',
		debutPeriode: '',
		finPeriode: '',
		jours: '',
		plat: '',
		showModal: false,
	},
	
	components: {
		'modal' : laModal,
	},
	
	created () {
		 fetch('api/hello', initPost)
			.then(response => response.json())
			.then(json => {
					this.menuSemaine = json;
					this.debutPeriode = json.debut;
					this.finPeriode = json.fin;
					this.jours = json.jours;
				}
			);
	},
	methods: {
		displayModal: function (plat) {
			//$modal.setNut(nut);
			this.plat = plat;
			console.log(this.plat);
			this.showModal = true;
		}
	}

})