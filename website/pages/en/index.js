const React = require('react');

const CompLibrary = require('../../core/CompLibrary.js');

const MarkdownBlock = CompLibrary.MarkdownBlock;
const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

class HomeSplash extends React.Component {
  render() {
    const { siteConfig, language = '' } = this.props;
    const { baseUrl, docsUrl } = siteConfig;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
    const langPart = `${language ? `${language}/` : ''}`;
    const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;

    const SplashContainer = props => (
      <div className="homeContainer">
        <div className="homeSplashFade">
          <div className="wrapper homeWrapper">{props.children}</div>
        </div>
      </div>
    );

    const ProjectTitle = () => (
      <h2 className="projectTitle">
        <span>
          <img className="projectTitleLogo" src={siteConfig.titleIcon} />
          {siteConfig.title}
        </span>
        <small>{siteConfig.tagline}</small>
      </h2>
    );

    const PromoSection = props => (
      <div className="section promoSection">
        <div className="promoRow">
          <div className="pluginRowBlock">{props.children}</div>
        </div>
      </div>
    );

    const Button = props => (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={props.href} target={props.target}>
          {props.children}
        </a>
      </div>
    );

    return (
      <SplashContainer>
        <div className="inner">
          <ProjectTitle siteConfig={siteConfig} />
          <PromoSection>
            <Button href={siteConfig.apiUrl}>API Docs</Button>
            <Button href={docUrl("overview", language)}>Documentation</Button>
            <Button href={siteConfig.repoUrl}>View on GitHub</Button>
          </PromoSection>
        </div>
      </SplashContainer>
    );
  }
}

class Index extends React.Component {
  render() {
    const { config: siteConfig, language = '' } = this.props;
    const { baseUrl } = siteConfig;

    const Block = props => (
      <Container
        padding={['bottom', 'top']}
        id={props.id}
        background={props.background}>
        <GridBlock
          align="center"
          contents={props.children}
          layout={props.layout}
        />
      </Container>
    );

    const index = `[![Build Status](https://travis-ci.com/pierrenodet/lunium.svg?branch=master)](https://travis-ci.com/pierrenodet/lunium)
    [![codecov](https://codecov.io/gh/pierrenodet/lunium/branch/master/graph/badge.svg)](https://codecov.io/gh/pierrenodet/lunium)
    [![Gitter](https://badges.gitter.im/pierrenodet-lunium/community.svg)](https://gitter.im/pierrenodet-lunium/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
    [![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://github.com/pierrenodet/lunium/blob/master/LICENSE)
    [![Maven Central](https://img.shields.io/maven-central/v/com.github.pierrenodet/lunium_2.12.svg?label=maven-central&colorB=blue)](https://search.maven.org/search?q=g:%22com.github.pierrenodet%22%20AND%20a:%22lunium_2.12%22)
      `.trim();

    return (
      <div>
        <HomeSplash siteConfig={siteConfig} language={language} />
        <div className="mainContainer">
          <div className="index">
            <MarkdownBlock>{index}</MarkdownBlock>
            <p>A retarded attempt to do a zero dependency, tagless and bifunctor based library for WebDrivers</p>
          </div>
        </div>
      </div>
    );
  }
}

module.exports = Index;
