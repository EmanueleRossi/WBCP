(function(){

	var util = {
		constants : {
			statusOptions : [
				{ name : "Segnazione ricevuta" , 					value : 0},
				{ name : "Pratica presa in carica" , 			value : 1},
				{ name : "Verifica identità segnalante" , value : 2},
				{ name : "Verifica di ammissibilità" , 		value : 3},
				{ name : "Apertura indagine" , 						value : 4},
				{ name : "Pratica non ammissibile" , 			value : 5},
				{ name : "Pratica risolta" , 							value : 6},
				{ name : "Pratica chiusa" , 							value : 7}
			],

			otherRecipientOptions : [
				{ name : "Resp. prevenzione della corruzione" } ,
				{ name : "Corte dei Conti" } ,
				{ name : "Autorità Giudiziaria" }
			],

			reasonOptions  : [
				{ name : "E' penalmente rilevante" } ,
				{ name : "Viola il Codice di comportamento o altre disposizioni sanzionabili in via disciplinare" } ,
				{ name : "Arreca un danno patrimoniale all’ente o altra amministrazione" } ,
				{ name : "Arreca un danno all’immagine dell’amministrazione" } ,
				{ name : "Viola le norme ambientali e di sicurezza sul lavoro" } ,
				{ name : "Costituisce un caso di malagestione delle risorse pubbliche (sprechi,mancato rispetto dei termini procedimentali, ecc.)" } ,
				{ name : "Costituisce una misura discriminatoria nei confronti del dipendente pubblico che ha segnalato illecito" } ,
				{ name : "Altro" } 
			],

			areaOptions: [
				{ name : "Reclutamento del personale" } ,
				{ name : "Contratti" } ,
				{ name : "Concessione di vantaggi economici comunque denominati" } ,
				{ name : "Autorizzazioni" } ,
				{ name : "Ispezioni" } ,
				{ name : "Altro" } 
			],

			sectorOptions : [
				{ name : "Sanità" } ,
				{ name : "Gestione del territorio" } ,
				{ name : "Protezione ambientale" } ,
				{ name : "Gestione dei rifiuti" } ,
				{ name : "Trasporti e Viabilità" } ,
				{ name : "Ordine pubblico" } ,
				{ name : "Telecomunicazioni" } ,
				{ name : "Politiche agricole e forestali" } ,
				{ name : "Beni e attività culturali" } ,
				{ name : "Sviluppo economico" } ,
				{ name : "Istruzione e Formazione" } ,
				{ name : "Gestione Finanziaria" } ,
				{ name : "Altro" }
			]
		}
	};

	app.util = util;

})()

