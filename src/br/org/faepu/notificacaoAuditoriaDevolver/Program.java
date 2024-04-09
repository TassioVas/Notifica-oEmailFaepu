package br.org.faepu.notificacaoAuditoriaDevolver;

import java.math.BigDecimal;
import java.sql.ResultSet;

import br.com.sankhya.checkout.util.JapeContext;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;

public class Program implements AcaoRotinaJava {

	BigDecimal nuFin;
	BigDecimal codUsuNota;
	BigDecimal nuNota;

	String obs;
	String emailNota;
	String nomeUsuNota;
	String msg;

	@Override
	public void doAction(ContextoAcao ctx) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		 * Codigo desenvolvido para Sankhya, serve para um botão de ação enviar uma notificação 
		 * diped atraves do portal de compras e das centrais, no modulo JAVA, e o modulo 16
		 * e o nome do arquivo JAR e o DEVOLVERNOTIFICACAO.JAR
		 */

		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		JapeSession.SessionHandle hnd = JapeSession.open();

		System.out.println("Inicio do codigo");

		Historicos his = new Historicos();
		EnviaEmail enviarEamil = new EnviaEmail();

		for (int i = 0; i < (ctx.getLinhas()).length; i++) {
			Registro linha = ctx.getLinhas()[i];

			nuNota = (BigDecimal) linha.getCampo("NUNOTA");
			System.out.println("nuNota : " + nuNota);
		}

		BigDecimal usuarioLogado = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();


		ResultSet user = nativeSql.executeQuery("SELECT CODUSU FROM TGFCAB where NUNOTA =" + nuNota);

		while (user.next()) {
			codUsuNota = user.getBigDecimal("CODUSU");
			System.out.println("Nunota :" + nuNota);
			System.out.println("Codusu : " + codUsuNota);
		}

		System.out.println("Usuario logado :" + usuarioLogado);

		try {

			ResultSet rsNufin = nativeSql.executeQuery("SELECT NUFIN FROM TGFFIN WHERE NUNOTA = " + nuNota);

			while (rsNufin.next()) {
				
				nuFin = rsNufin.getBigDecimal("NUFIN");

				boolean update = nativeSql.executeUpdate("UPDATE TGFFIN SET AD_NOTIFICAR = 2  WHERE NUFIN = " + nuFin);
			}

			System.out.println("aPOS O UPDATE");

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}

		enviarEamil.EnviarEmail(usuarioLogado, nuFin, emailNota, nomeUsuNota, nuNota, obs);
		his.InserirHistorico(usuarioLogado, nuFin, nuNota);
		
		boolean disable = nativeSql.executeUpdate("ALTER TABLE TGFCAB DISABLE TRIGGER TRG_FAE_UPD_TGFCAB");
		boolean update4 = nativeSql.executeUpdate("UPDATE TGFCAB SET AD_IMPOSTOS = NULL WHERE NUNOTA = " + nuNota);
		boolean enable = nativeSql.executeUpdate("ALTER TABLE TGFCAB ENABLE  TRIGGER TRG_FAE_UPD_TGFCAB");
		
		boolean update = nativeSql.executeUpdate("UPDATE TGFFIN SET AD_IMPREA = 2 WHERE NUNOTA = " + nuNota);

		ctx.setMensagemRetorno("DEVOLVIDA COM SUCESSO!");

		JapeSession.close(hnd);
	}
}
