<?xml version='1.0'?>
<!-- :folding=explicit: -->
<!-- You should use this XSL stylesheet to create plugin documentation.

     If you want all output in a single HTML file, specify the path to
     your DocBook-XSL "html/docbook.xsl" file in the <xsl:import>
     statement below. If you want each chapter to have its own file,
     specify the path to your "html/chunk.xsl".

	 This stylesheet assumes the user's guide XML source is in a
	 subdirectory of the plugin's main dir (e.g., "docs/users-guide.xml").
-->

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	xmlns="http://www.w3.org/TR/xhtml1/transitional"
	xmlns:date="http://exslt.org/dates-and-times"
	extension-element-prefixes="date"
	exclude-result-prefixes="#default">

	<xsl:import href="file:///@docs.style.sheet@" />

	<!-- chunk by chapters instead of sections by setting chunk.section.depth to 0
	     instead of the default 1 -->
	<xsl:param name="chunk.section.depth" select="1"/>
	
	<!-- Swing HTML control doesn't support &ldquo; and &rdquo; -->
	<xsl:template match="quote">&quot;<xsl:apply-templates/>&quot;</xsl:template>

	<xsl:template match="guibutton">
		<xsl:call-template name="inline.boldseq"/>
	</xsl:template>

	<xsl:template match="guiicon">
		<xsl:call-template name="inline.boldseq"/>
	</xsl:template>

	<xsl:template match="guilabel">
		<xsl:call-template name="inline.boldseq"/>
	</xsl:template>

	<xsl:template match="guimenu">
		<xsl:call-template name="inline.boldseq"/>
	</xsl:template>

	<xsl:template match="guimenuitem">
		<xsl:call-template name="inline.boldseq"/>
	</xsl:template>

	<xsl:template match="guisubmenu">
		<xsl:call-template name="inline.boldseq"/>
	</xsl:template>

	<xsl:template match="image">
		<p>
			<center>
				<img src="{src}" />
			</center>
		</p>
	</xsl:template>

	<xsl:template match="br">
		<br />
	</xsl:template>

	<xsl:param name="toc.list.type">ul</xsl:param>
	<xsl:param name="use.id.as.filename">1</xsl:param>
	<xsl:param name="shade.verbatim">1</xsl:param>

	<xsl:param name="funcsynopsis.style">ansi</xsl:param>
	<xsl:template match="void">
		<xsl:apply-templates/>
	</xsl:template>


	<xsl:param name="chunk.first.sections">1</xsl:param>

	<xsl:template match="*" mode="object.title.markup.textonly">
		<xsl:variable name="title">
			<xsl:apply-templates select="." mode="title.markup"/>
		</xsl:variable>
  <!-- used to get the html title of the page -->
		<xsl:value-of select="$title"/>
	</xsl:template>

<!-- {{{ TOC generation -->
	<xsl:template match="/">
		<xsl:call-template name="toc"/>
	</xsl:template>

	<xsl:template name="toc">
		<xsl:apply-templates/>
		<xsl:call-template name="write.chunk">
			<xsl:with-param name="filename" select="'toc.xml'"/>
			<xsl:with-param name="method" select="'xml'"/>
			<xsl:with-param name="indent" select="'yes'"/>
			<xsl:with-param name="content">
				<xsl:call-template name="toc.content"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="toc.content">
		<TOC>
			<xsl:apply-templates select="." mode="my.toc"/>
		</TOC>
	</xsl:template>

	<xsl:template match="set" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="book" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="book" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="part|reference|preface|chapter|appendix|article|colophon" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="part|reference|preface|chapter|appendix|article" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="preface|chapter|appendix|refentry|section|sect1" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="section" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="section" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="sect1" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="sect2" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="sect2" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="sect3" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="sect3" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="sect4" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="sect4" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
			<xsl:apply-templates select="sect5" mode="my.toc"/>
		</ENTRY>
	</xsl:template>

	<xsl:template match="sect5|colophon" mode="my.toc">
		<ENTRY>
			<xsl:attribute name="HREF">
				<xsl:call-template name="href.target">
					<xsl:with-param name="object" select="."/>
				</xsl:call-template>
			</xsl:attribute>
			<TITLE>
				<xsl:apply-templates mode="title.markup" select="."/>
			</TITLE>
		</ENTRY>
	</xsl:template>

<!-- }}} -->

<!-- {{{ Dale's gorgeous HTMl template -->

	<!-- title banner, from docbook/html/titlepage.templates.xsl -->
	<xsl:template name="article.titlepage.recto">
		<xsl:call-template name="article-or-book.titlepage.recto"/>
	</xsl:template>
	
	<xsl:template name="book.titlepage.recto">
		<xsl:call-template name="article-or-book.titlepage.recto"/>
	</xsl:template>

	<xsl:template name="article-or-book.titlepage.recto">
		<table summary="Header" cellspacing="0" border="0" width="100%" cols="2">
			<tr width="100%"  bgcolor="#CCCCFF">
				<!-- left cell : title, copyright -->
				<td valign="TOP">
					<xsl:choose>
						<xsl:when test="bookinfo/title">
							<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="bookinfo/title"/>
						</xsl:when>
						<xsl:when test="articleinfo/title">
							<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/title"/>
						</xsl:when>
						<xsl:when test="artheader/title">
							<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/title"/>
						</xsl:when>
						<xsl:when test="info/title">
							<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/title"/>
						</xsl:when>
						<xsl:when test="title">
							<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="title"/>
						</xsl:when>
					</xsl:choose>
					
					<!-- maybe the copyright notice should go on another line... -->
					<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/copyright"/>
					<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="bookinfo/copyright"/>
					<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/copyright"/>
					<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/copyright"/>
				</td>
				<!-- right cell : version, generation date and authors -->
				<td valign="TOP" align="RIGHT">
					<font size="-1">
						<p>
							<strong>
							<!-- release -->
								<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/releaseinfo"/>
								<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="bookinfo/releaseinfo"/>
								<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/releaseinfo"/>
								<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/releaseinfo"/>

								<!-- compute release date automatically -->
								<xsl:text> (</xsl:text>
								<xsl:value-of select="concat(date:month-name(),' ',date:day-in-month(), ' ', date:year())"/>
								<xsl:text>)</xsl:text>
							</strong>
						</p>

						<!-- hackish list of authors -->
						<xsl:for-each select="//author">
							<p>
								<xsl:apply-templates select="."/>
							</p>
						</xsl:for-each>
					</font>
				</td>
			</tr>
		</table>

		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/legalnotice"/>
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/legalnotice"/>
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/legalnotice"/>
		
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/revhistory"/>
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/revhistory"/>
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/revhistory"/>
		
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/abstract"/>
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/abstract"/>
		<xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/abstract"/>
	</xsl:template>

	<xsl:template match="title" mode="article.titlepage.recto.auto.mode">
		<strong>
			<font size="+2">
				<xsl:value-of select="."/>
			</font>
		</strong>
	</xsl:template>
	<xsl:template match="releaseinfo" mode="article.titlepage.recto.auto.mode">
		<xsl:apply-templates/>
	</xsl:template>

	<!--  whole line white on blue banner for the legal notice
	     overrides docbook-xsl/html/titlepage.xsl
	     disabled because I prefer a standard legal notice title
	 -->
	<!--<xsl:template match="legalnotice/title" mode="titlepage.mode">
		<xsl:call-template name="section.heading">
			<xsl:with-param name="section" select="parent::*"/>
			<xsl:with-param name="allow-anchors" select="0"/>
			<xsl:with-param name="title" select="string(.)"/>
		</xsl:call-template>
	</xsl:template>-->

	<!-- whole line white on blue banner for each (sub)section
	     overrides docbook-xsl/html/sections.xsl
	  -->
	<xsl:template name="section.heading">
		<xsl:param name="section" select="."/>
		<xsl:param name="level" select="1"/>
		<xsl:param name="allow-anchors" select="1"/>
		<xsl:param name="title"/>
		<xsl:param name="class" select="'title'"/>

		<xsl:variable name="id">
			<xsl:choose>
      <!-- Make sure the subtitle doesn't get the same id as the title -->
				<xsl:when test="self::subtitle">
					<xsl:call-template name="object.id">
						<xsl:with-param name="object" select="."/>
					</xsl:call-template>
				</xsl:when>
      <!-- if title is in an *info wrapper, get the grandparent -->
				<xsl:when test="contains(local-name(..), 'info')">
					<xsl:call-template name="object.id">
						<xsl:with-param name="object" select="../.."/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="object.id">
						<xsl:with-param name="object" select=".."/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<table summary="{.}" border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr align="CENTER" width="100%">
				<td bgcolor="#7FB2FF" width="100%">
					<strong>
						<font color="#FFFFFF" size="+1">
							<span>
								<xsl:if test="$allow-anchors != 0 and $generate.id.attributes = 0">
									<xsl:call-template name="anchor">
										<xsl:with-param name="node" select="$section"/>
										<xsl:with-param name="conditional" select="0"/>
									</xsl:call-template>
								</xsl:if>
								<xsl:if test="$generate.id.attributes != 0 and not(local-name(.) = 'appendix')">
									<xsl:attribute name="id">
										<xsl:value-of select="$id"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:copy-of select="$title"/>
							</span>
						</font>
					</strong>
				</td>
			</tr>
		</table>
	</xsl:template>
	
<!-- for chapter titles,
     overrides docbook-xsl/html/component.xsl
  -->
<xsl:template name="component.title">
  <xsl:param name="node" select="."/>

   <xsl:call-template name="section.heading">
   	<xsl:with-param name="title">
      <xsl:apply-templates select="$node" mode="object.title.markup">
    	  <xsl:with-param name="allow-anchors" select="1"/>
      </xsl:apply-templates>
    </xsl:with-param>
    <xsl:with-param name="section" select="$node"/>
    </xsl:call-template>
    
</xsl:template>


<!-- }}} -->
</xsl:stylesheet>
